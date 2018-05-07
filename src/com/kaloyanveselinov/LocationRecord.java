package com.kaloyanveselinov;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationRecord {
    private double certainty, accuracyRange;
    private double latitude, longitude;
    private byte source;
    private long timestamp;

    public LocationRecord(JSONObject locationRecord){
        try{
            this.latitude = locationRecord.getDouble("latitude");
            this.longitude = locationRecord.getDouble("longitude");
            this.source = Byte.valueOf(locationRecord.getString("source"));
            this.certainty = locationRecord.getDouble("certainty");
            this.accuracyRange = locationRecord.getDouble("accuracyRange");
            this.timestamp = locationRecord.getLong("timestamp");
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

}
