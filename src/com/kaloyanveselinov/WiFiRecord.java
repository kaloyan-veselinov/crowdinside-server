package com.kaloyanveselinov;

import org.json.JSONException;
import org.json.JSONObject;

public class WiFiRecord {
    private String bssid;
    private int level;
    private int channel;

    public WiFiRecord(String bssid, int level, int channel){
        this.bssid = bssid;
        this.level = level;
        this.channel = channel;
    }

    public WiFiRecord(JSONObject wifiData){
        try {
            this.bssid = wifiData.getString("bssid");
            this.level = wifiData.getInt("level");
            this.channel = wifiData.getInt("channel");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
