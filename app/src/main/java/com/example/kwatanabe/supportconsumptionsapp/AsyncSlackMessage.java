package com.example.kwatanabe.supportconsumptionsapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackPreparedMessage;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by kwatanabe on 2017/04/04.
 */

public class AsyncSlackMessage extends AsyncTask<String, Void, Void> {

    private Context mContext;
    private Commodity mCommodity;
    MyProgressDialogFragment progressDialog = null;    // ロード中画面のプログレスダイアログ作成


    /**
     * コンストラクタ
     */
    public AsyncSlackMessage(Context context, Commodity commodity) {
        super();
        mContext = context;
        mCommodity = commodity;
    }

    /**
     * バッググラウンド処理の前処理（準備）
     * UI Thread処理
     */
    @Override
    protected void onPreExecute() {
        @SuppressWarnings({"serial"})
        Serializable Cancel_Listener = new MyProgressDialogFragment.CancelListener() {
            @Override
            public void canceled(DialogInterface _interface) {
                cancel(true); // これをTrueにすることでキャンセルされ、onCancelledが呼び出される。
            }
        };
        progressDialog = MyProgressDialogFragment.newInstance("Now sending a messege to Slack...", "Please wait for a while.", true, Cancel_Listener);
        progressDialog.show(((Activity) mContext).getFragmentManager(), "progress");
    }


    @Override
    protected Void doInBackground(String... params) {
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(mContext.getString(R.string.slack_token));
        try {
            session.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SlackChannel channel = session.findChannelByName(mCommodity.getChannel_name()); //make sure bot is a member of the channel.

        //build a message object
        SlackPreparedMessage preparedMessage = new SlackPreparedMessage.Builder()
                .withMessage(params[0])
                .withUnfurl(false)
                .withLinkNames(true)
                .build();

        session.sendMessage(channel, preparedMessage);
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        if (progressDialog.getShowsDialog())
            progressDialog.dismiss();
        String sep = System.getProperty("line.separator");
        Toast.makeText(this.mContext, "Succsessfully sent a message！" + sep + mCommodity.toMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * 中止された際の処理
     */
    @Override
    protected void onCancelled() {
        if (progressDialog.getShowsDialog()) {
            progressDialog.dismiss();
        }
        Toast.makeText(mContext, "Canceled", Toast.LENGTH_SHORT).show();
    }
}
