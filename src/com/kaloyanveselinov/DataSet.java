package com.kaloyanveselinov;

import org.json.JSONObject;

import java.io.*;
import java.util.LinkedList;

class DataSet {
    LinkedList<InertialSensorRecord> accelerometerData;
    LinkedList<InertialSensorRecord> magnetometerData;
    LinkedList<InertialSensorRecord> gyroscopeData;
    LinkedList<LocationRecord> gpsData;
    LinkedList<WiFiRecord> wifiData;

    DataSet(File dataFile){
        accelerometerData = new LinkedList<>();
        magnetometerData = new LinkedList<>();
        gyroscopeData = new LinkedList<>();
        gpsData = new LinkedList<>();
        wifiData = new LinkedList<>();
        try {
            FileReader fileReader = new FileReader(dataFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line = bufferedReader.readLine()) != null){
                parseLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine(String line){
        JSONObject parsedLine = new JSONObject(line);
        String sensorType = parsedLine.getString("sensorType");
        long timestamp = parsedLine.getLong("timestamp");
        JSONObject data = new JSONObject(parsedLine.getString("data"));
        switch (sensorType){
            case "accelerometer":
                accelerometerData.add(new InertialSensorRecord(data, sensorType, timestamp));
                break;
            case "magnetometer":
                magnetometerData.add(new InertialSensorRecord(data, sensorType, timestamp));
                break;
            case "gyroscope":
                gyroscopeData.add(new InertialSensorRecord(data, sensorType, timestamp));
                break;
            case "wifiAP":
                wifiData.add(new WiFiRecord(data, timestamp));
                break;
            case "GPS":
                gpsData.add(new LocationRecord(data, timestamp));
                break;
        }
    }
}
