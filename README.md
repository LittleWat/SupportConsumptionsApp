# SupportConsumptionsApp

This android app notificates the shortage of daily necessities through Slack. 
You can open this project using AndroidStudio.

In this app, [simple slack api](https://github.com/Ullink/simple-slack-api) is used.

![Demo](https://github.com/LittleWat/SupportConsumptionsApp/blob/master/2017_05_14_15_37_46.gif)


## How to Use

First, you have to get Slack API Legacy token [here](https://api.slack.com/custom-integrations/legacy-tokens).

Next, at SupportCunsumption directory, copy the files like below.

```
cp ./app/src/main/assets/template_info.json ./app/src/main/assets/WRITEME_info.json
cp ./app/src/main/res/values/template_strings.xml ./app/src/main/res/values/strings.xml
```

Then, rewrite these for you to match your settings.

That's all :)