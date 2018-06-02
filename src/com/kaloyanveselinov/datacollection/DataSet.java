package com.kaloyanveselinov.datacollection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;

public class DataSet {
    private LinkedList<InertialSensorRecord> accelerometerData;
    private LinkedList<InertialSensorRecord> magnetometerData;
    private LinkedList<InertialSensorRecord> gyroscopeData;
    private LinkedList<AggregatedReading> aggregatedReadings;
    private final int AGGREGATE_INTERVAL = 500;
    private LinkedList<LocationRecord> gpsData;
    private LinkedList<WiFiScan> wifiData;
    private Timestamp timestamp;

    public DataSet(File dataFile) {
        accelerometerData = new LinkedList<>();
        magnetometerData = new LinkedList<>();
        gyroscopeData = new LinkedList<>();
        aggregatedReadings = new LinkedList<>();
        gpsData = new LinkedList<>();
        wifiData = new LinkedList<>();
        try {
            FileReader fileReader = new FileReader(dataFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            JSONObject identifier = new JSONObject(line);
            timestamp = Timestamp.valueOf(identifier.getString("timestamp"));
            System.out.println(timestamp.getTime());
            while ((line = bufferedReader.readLine()) != null) {
                parseLine(line);
            }
            bufferedReader.close();
            aggregateReadings();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine(String line) {
        JSONObject parsedLine = new JSONObject(line);
        String sensorType = parsedLine.getString("sensorType");
        Timestamp timestamp = new Timestamp(parsedLine.getLong("timestamp"));
        JSONObject data = parsedLine.getJSONObject("data");
        switch (sensorType) {
            case "accelerometer":
                accelerometerData.add(new InertialSensorRecord(data, timestamp));
                break;
            case "magnetometer":
                magnetometerData.add(new InertialSensorRecord(data, timestamp));
                break;
            case "gyroscope":
                gyroscopeData.add(new InertialSensorRecord(data, timestamp));
                break;
            case "wifiAP":
                wifiData.add(new WiFiScan(data, timestamp));
                break;
            case "GPS":
                gpsData.add(new LocationRecord(data, timestamp));
                break;
        }
    }

    public void aggregateReadings() {
        Iterator<InertialSensorRecord> accI = accelerometerData.iterator();
        Iterator<InertialSensorRecord> gyroI = gyroscopeData.iterator();
        Iterator<InertialSensorRecord> magnI = magnetometerData.iterator();
        long time = timestamp.getTime();
        long maxTime;
        while (accI.hasNext() || gyroI.hasNext() || magnI.hasNext()) {
            maxTime = time + AGGREGATE_INTERVAL;
            AggregatedReading aggregatedReading = new AggregatedReading(new Timestamp(time));
            aggregatedReading.setAcceleration(accI, maxTime);
            aggregatedReading.setMagnetometer(magnI, maxTime);
            aggregatedReading.setGyroscope(gyroI, maxTime);
            aggregatedReadings.add(aggregatedReading);
            time = maxTime;
        }
    }

    public void toCSV() {
        String fileSuffix = "_" + timestamp.toString().substring(0, timestamp.toString().length() - 4).replaceAll(":", "-").replaceAll(" ", "_") + ".csv";
        InertialSensorRecord.toCSV(accelerometerData, "accelerometer", "accelerometer" + fileSuffix);
        InertialSensorRecord.toCSV(magnetometerData, "magnetometer", "magnetometer" + fileSuffix);
        InertialSensorRecord.toCSV(gyroscopeData, "gyroscope", "gyroscope" + fileSuffix);
        AggregatedReading.toCSV(aggregatedReadings, fileSuffix);
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }


    public LinkedList<AggregatedReading> getAggregatedReadings() {
        return aggregatedReadings;
    }

}
