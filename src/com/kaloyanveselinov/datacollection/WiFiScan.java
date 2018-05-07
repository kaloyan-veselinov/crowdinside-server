package com.kaloyanveselinov.datacollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class WiFiScan {

    public class WiFiRecord{
        private String bssid;
        private int level;
        private int channel;

        public WiFiRecord(JSONObject wiFiAPData){
            try {
                this.bssid = wiFiAPData.getString("bssid");
                this.level = wiFiAPData.getInt("level");
                this.channel = wiFiAPData.getInt("channel");
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private LinkedList<WiFiRecord> wifiAccessPoints;
    private long timestamp;

    public WiFiScan(JSONObject wifiScanData, long timestamp){
        this.timestamp = timestamp;
        wifiAccessPoints = new LinkedList<>();
        try{
            JSONArray accessPointsDataArray = wifiScanData.getJSONArray("wifiAPData");
            for(int i=0; i<accessPointsDataArray.length(); i++){
                JSONObject accessPointData = accessPointsDataArray.getJSONObject(i);
                wifiAccessPoints.add(new WiFiRecord(accessPointData));
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public LinkedList<WiFiRecord> getWifiAccessPoints() {
        return wifiAccessPoints;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
