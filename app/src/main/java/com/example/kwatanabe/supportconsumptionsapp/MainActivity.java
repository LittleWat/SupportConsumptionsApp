package com.example.kwatanabe.supportconsumptionsapp;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private List<Commodity> getCommodityListFromJson(String jsonFilename) {
        List<Commodity> res = new ArrayList<>();

        AssetManager assetManager = getResources().getAssets();

        String jsonString = null;
        try {
            // assets/json01.jsonを読む
            InputStream inputStream = assetManager.open(jsonFilename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            jsonString = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("JsonString", jsonString);

        if (jsonString == null) return res;

        JSONObject json = null;
        try {
            json = new JSONObject(jsonString);

            Iterator iterator = json.keys();
            while (iterator.hasNext()) {
                String channelName = (String) iterator.next();
                JSONObject channelJson = json.getJSONObject(channelName);

                JSONArray items = channelJson.getJSONArray("items");
                String staffName = channelJson.optString("staff");
                Log.d("items", items.toString());

                for (int i = 0; i < items.length(); i++) {
                    String item = items.getString(i);
                    res.add(new Commodity(item, channelName, staffName));
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        List<Commodity> commodityList = getCommodityListFromJson(getString(R.string.jsonFilename));
        Log.d("CommodityList", commodityList.toString());

        GridView gridView = (GridView) findViewById(R.id.commoditysGridView);
        CommodityGridContentAdapter listAdapter = new CommodityGridContentAdapter();

        listAdapter.setCommoditys(commodityList);
        gridView.setAdapter(listAdapter);

    }

    private class CommodityGridContentAdapter extends BaseAdapter {
        private List<Commodity> commoditys = new ArrayList<Commodity>();

        void setCommoditys(List<Commodity> commoditys) {
            this.commoditys = commoditys;
        }

        @Override
        public int getCount() {
            return commoditys.size();
        }

        @Override
        public Object getItem(int position) {
            return commoditys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linearLayout = new LinearLayout(MainActivity.this);
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            final Commodity commodity = (Commodity) getItem(position);

            if (commodity != null) {
                Button button = new Button(MainActivity.this);
                button.setText(commodity.getName());

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(commodity.getName() + " is selected")
                                .setMessage("Are you sure to send a slack message to " + commodity.getStaff_name() + " at #" + commodity.getChannel_name() + " channel ?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // OK button pressed
                                        AsyncSlackMessage asyncSlackMessage = new AsyncSlackMessage(MainActivity.this, commodity);
                                        asyncSlackMessage.execute(commodity.toMessage());
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                });

                linearLayout.addView(button, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 200));
            }

            convertView = linearLayout;

            return convertView;
        }
    }
}

