package com.example.kwatanabe.supportconsumptionsapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwatanabe on 2017/05/12.
 */

public class Commodity {

    private String name;
    private String channel_name;
    private String staff_name;

    private Map<String, String> channel2staff = new HashMap<String, String>() {
        {
            put("3_storekeeper", "machida");
            put("3_milbank", "arai");
            put("6_watanabe", "kwatanabe");
        }
    };


    public Commodity(String name, String channel_name) {
        this.name = name;
        this.channel_name = channel_name;
        this.staff_name = channel2staff.get(channel_name);
    }


    public Commodity(String name, String channel_name, String staff_name) {
        this.name = name;
        this.channel_name = channel_name;
        this.staff_name = staff_name;
    }


    public String toMessage() {
        String sep = System.getProperty("line.separator");

        return "@" + staff_name + sep
                + "### " + name + " is now in short ###";
    }

    public String getName() {
        return name;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public String getChannel_name() {
        return channel_name;
    }
}
