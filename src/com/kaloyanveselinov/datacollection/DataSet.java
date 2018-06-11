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
    private LinkedList<LocationRecord> gpsData;
    private LinkedList<WiFiScan> wifiData;
    private int aggregationTime = 0;
    private Timestamp timestamp;
    private String type = "";

    public DataSet(File dataFile) {
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
            timestamp = Timestamp.valueOf(identifier.getString("timestamp"));
            System.out.println(timestamp.getTime());
            while ((line = bufferedReader.readLine()) != null) {
                parseLine(line);
            }
            bufferedReader.close();
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
                accelerometerData.add(new InertialSensorRecord(data, this.timestamp, timestamp));
                break;
            case "magnetometer":
                magnetometerData.add(new InertialSensorRecord(data, this.timestamp, timestamp));
                break;
            case "gyroscope":
                gyroscopeData.add(new InertialSensorRecord(data, this.timestamp, timestamp));
                break;
            case "wifiAP":
                wifiData.add(new WiFiScan(data, timestamp));
                break;
            case "GPS":
                gpsData.add(new LocationRecord(data, timestamp));
                break;
        }
    }

    public LinkedList<AggregatedReading> aggregateReadings(int aggregateInterval) {
        LinkedList<AggregatedReading> aggregatedReadings = new LinkedList<>();
        Iterator<InertialSensorRecord> accI = accelerometerData.iterator();
        Iterator<InertialSensorRecord> gyroI = gyroscopeData.iterator();
        Iterator<InertialSensorRecord> magnI = magnetometerData.iterator();
        long time = timestamp.getTime();
        long maxTime;
        while (accI.hasNext() || gyroI.hasNext() || magnI.hasNext()) {
            maxTime = time + aggregateInterval;
            AggregatedReading aggregatedReading = new AggregatedReading(this.timestamp, new Timestamp(time));
            aggregatedReading.setAcceleration(accI, maxTime);
            aggregatedReading.setMagnetometer(magnI, maxTime);
            aggregatedReading.setGyroscope(gyroI, maxTime);
            aggregatedReadings.add(aggregatedReading);
            time = maxTime;
        }
        return aggregatedReadings;
    }

    public void toCSV() {
        String fileSuffix = "_" + timestamp.toString().substring(0, timestamp.toString().length() - 4).replaceAll(":", "-").replaceAll(" ", "_") + ".csv";
        InertialSensorRecord.toCSV(accelerometerData, "accelerometer", "accelerometer" + fileSuffix);
        InertialSensorRecord.toCSV(magnetometerData, "magnetometer", "magnetometer" + fileSuffix);
        InertialSensorRecord.toCSV(gyroscopeData, "gyroscope", "gyroscope" + fileSuffix);
        AggregatedReading.toCSV(aggregateReadings(aggregationTime), fileSuffix, type);
    }

    public void setAggregationTime(int aggregationTime) {
        this.aggregationTime = aggregationTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

}
