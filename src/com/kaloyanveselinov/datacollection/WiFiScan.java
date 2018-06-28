package com.kaloyanveselinov.datacollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.LinkedList;

/**
 * Class for a WiFi scan produced by the app
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 */
public class WiFiScan {
    private LinkedList<WiFiRecord> wifiAccessPoints;
    private Timestamp timestamp;

    /**
     * Parses the WiFi scan from the JSONObject
     * @param wifiScanData the WiFi scan to parse
     * @param timestamp the time of the reading
     */
    WiFiScan(JSONObject wifiScanData, Timestamp timestamp){
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Class for a single WiFi access point data in the scan
     */
    public class WiFiRecord{
        private String bssid;
        private int level;
        private int channel;

        WiFiRecord(JSONObject wiFiAPData){
            try {
                this.bssid = wiFiAPData.getString("bssid");
                this.level = wiFiAPData.getInt("level");
                this.channel = wiFiAPData.getInt("channel");
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}
