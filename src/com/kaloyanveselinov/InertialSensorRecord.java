package com.kaloyanveselinov;

import org.json.JSONException;
import org.json.JSONObject;

class InertialSensorRecord {
    private String type;
    private float x, y, z;
    private long timestamp;
    private long sensorTimestamp;

    InertialSensorRecord(JSONObject sensorData, String sensorType, long timestamp){
        try {
            this.type = sensorType;
            this.x = sensorData.getFloat("xValue");
            this.y = sensorData.getFloat("yValue");
            this.z = sensorData.getFloat("zValue");
            this.timestamp = timestamp;
            this.sensorTimestamp = sensorData.getLong("sensorTimestamp");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getSensorTimestamp() {
        return sensorTimestamp;
    }
}
