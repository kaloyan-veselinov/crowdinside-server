package com.kaloyanveselinov.datacollection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class DataSet {
    private LinkedList<InertialSensorRecord> accelerometerData;
    private LinkedList<InertialSensorRecord> magnetometerData;
    private LinkedList<InertialSensorRecord> gyroscopeData;
    private LinkedList<LocationRecord> gpsData;
    private LinkedList<WiFiScan> wifiData;
    private String phoneId, buildingName;
    private String timestamp;

    public DataSet(File dataFile){
        accelerometerData = new LinkedList<>();
        magnetometerData = new LinkedList<>();
        gyroscopeData = new LinkedList<>();
        gpsData = new LinkedList<>();
        wifiData = new LinkedList<>();
        try {
            FileReader fileReader = new FileReader(dataFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            JSONObject identifier = new JSONObject(line);
            phoneId = identifier.getString("phoneID");
            buildingName = identifier.getString("buildingName");
            timestamp = identifier.getString("timestamp");
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
        JSONObject data = parsedLine.getJSONObject("data");
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
                wifiData.add(new WiFiScan(data, timestamp));
                break;
            case "GPS":
                gpsData.add(new LocationRecord(data, timestamp));
                break;
        }
    }

    public void toCSV(){
        String fileSuffix = "_" + timestamp.substring(0, timestamp.length() - 4).replaceAll(":", "-").replaceAll(" ", "_") + ".csv";
        InertialSensorRecord.toCSV(accelerometerData, "accelerometer", "accelerometer" + fileSuffix);
        InertialSensorRecord.toCSV(magnetometerData, "magnetometer", "magnetometer" + fileSuffix);
        InertialSensorRecord.toCSV(gyroscopeData, "gyroscope", "gyroscope" + fileSuffix);
    }


    public LinkedList<InertialSensorRecord> getAccelerometerData() {
        return accelerometerData;
    }

    public LinkedList<InertialSensorRecord> getMagnetometerData() {
        return magnetometerData;
    }

    public LinkedList<InertialSensorRecord> getGyroscopeData() {
        return gyroscopeData;
    }

    public LinkedList<LocationRecord> getGpsData() {
        return gpsData;
    }

    public LinkedList<WiFiScan> getWifiData() {
        return wifiData;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
