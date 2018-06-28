package com.kaloyanveselinov.datacollection;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * Class for a raw GPS reading
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 */
class LocationRecord {
    private double certainty, accuracyRange;
    private double latitude, longitude;
    private byte source;
    private Timestamp timestamp;

    /**
     * Parses the GPS reading in the JSONObject
     * @param locationRecord the GPS reading to parse
     * @param timestamp the timestamp of the reading
     */
    LocationRecord(JSONObject locationRecord, Timestamp timestamp){
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

    public double getCertainty() {
        return certainty;
    }

    public double getAccuracyRange() {
        return accuracyRange;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public byte getSource() {
        return source;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
