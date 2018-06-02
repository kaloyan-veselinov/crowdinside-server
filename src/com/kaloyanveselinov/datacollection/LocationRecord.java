package com.kaloyanveselinov.datacollection;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class LocationRecord {
    private double certainty, accuracyRange;
    private double latitude, longitude;
    private byte source;
    private Timestamp timestamp;

    public LocationRecord(JSONObject locationRecord, Timestamp timestamp){
        try{
            this.latitude = locationRecord.getDouble("latitude");
            this.longitude = locationRecord.getDouble("longitude");
            this.source = Byte.valueOf(locationRecord.getString("source"));
            this.certainty = locationRecord.getDouble("certainty");
            this.accuracyRange = locationRecord.getDouble("accuracyRange");
            this.timestamp = timestamp;
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

}
