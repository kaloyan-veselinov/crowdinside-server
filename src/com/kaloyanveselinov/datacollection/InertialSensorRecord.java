package com.kaloyanveselinov.datacollection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class InertialSensorRecord {
    private String type;
    private float x, y, z;
    private long timestamp;
    private long sensorTimestamp;

    public InertialSensorRecord(JSONObject sensorData, String sensorType, long timestamp){
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

    public static void toCSV(List<InertialSensorRecord> inertialSensorRecordList, String type, String filename){
        try {
            String[] HEADERS = {"timestamp", "x " + type + " value", "y " + type + " value", "z " + type + " value"};
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
            CSVPrinter accelerationPrinter = new CSVPrinter(bufferedWriter, CSVFormat.EXCEL.withHeader(HEADERS));
            for (InertialSensorRecord sensorRecord : inertialSensorRecordList) {
                accelerationPrinter.printRecord(sensorRecord.getSensorTimestamp(), sensorRecord.getX(), sensorRecord.getY(), sensorRecord.getZ());
            }
        } catch (IOException e) {
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
