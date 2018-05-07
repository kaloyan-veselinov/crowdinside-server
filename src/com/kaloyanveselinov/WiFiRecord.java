package com.kaloyanveselinov;

import org.json.JSONException;
import org.json.JSONObject;

class WiFiRecord {
    private String bssid;
    private int level;
    private int channel;

    WiFiRecord(String bssid, int level, int channel){
        this.bssid = bssid;
        this.level = level;
        this.channel = channel;
    }

    WiFiRecord(JSONObject wifiData){
        try {
            this.bssid = wifiData.getString("bssid");
            this.level = wifiData.getInt("level");
            this.channel = wifiData.getInt("channel");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
