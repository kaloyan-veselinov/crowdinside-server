package com.kaloyanveselinov.datacollection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class InertialSensorRecord {
    private Timestamp timestamp;
    private Vector3D vector;

    public InertialSensorRecord(JSONObject sensorData, Timestamp timestamp) {
        try {
            this.vector = new Vector3D(sensorData.getFloat("xValue"), sensorData.getFloat("yValue"), sensorData.getFloat("zValue"));
            this.timestamp = timestamp;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void toCSV(List<InertialSensorRecord> inertialSensorRecordList, String type, String filename) {
        try {
            String[] HEADERS = {"timestamp", "x " + type + " value", "y " + type + " value", "z " + type + " value"};
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
            CSVPrinter accelerationPrinter = new CSVPrinter(bufferedWriter, CSVFormat.EXCEL.withHeader(HEADERS));
            for (InertialSensorRecord sensorRecord : inertialSensorRecordList) {
                accelerationPrinter.printRecord(sensorRecord.timestamp.getTime(), sensorRecord.getX(), sensorRecord.getY(), sensorRecord.getZ());
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getX() {
        return vector.getX();
    }

    public double getY() {
        return vector.getY();
    }

    public double getZ() {
        return vector.getZ();
    }

    public long getTime() {
        return timestamp.getTime();
    }


}
