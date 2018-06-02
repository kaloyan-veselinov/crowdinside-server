package com.kaloyanveselinov.datacollection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class InertialSensorRecord {
    private float x;
    private float y;
    private float z;
    private Timestamp timestamp;

    public InertialSensorRecord(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = new Timestamp(0);
    }

    public InertialSensorRecord(JSONObject sensorData, Timestamp timestamp){
        try {
            this.x = sensorData.getFloat("xValue");
            this.y = sensorData.getFloat("yValue");
            this.z = sensorData.getFloat("zValue");
            this.timestamp = timestamp;
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
                accelerationPrinter.printRecord(sensorRecord.timestamp.getTime(), sensorRecord.x, sensorRecord.y, sensorRecord.z);
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


    public long getTime() {
        return timestamp.getTime();
    }


}
