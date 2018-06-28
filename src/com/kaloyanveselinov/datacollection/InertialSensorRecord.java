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

/**
 * Class for raw inertial sensor data parsed from the JSON data set
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 */
class InertialSensorRecord {
    private Timestamp timestamp;
    private Timestamp startTimestamp;
    private Vector3D vector;

    /**
     * Parses the JSONObject corresponding to the sensor record
     * @param sensorData the JSONObject to parse
     * @param startTimestamp the zero time value
     * @param timestamp the timestamp of the reading
     */
    InertialSensorRecord(JSONObject sensorData, Timestamp startTimestamp, Timestamp timestamp) {
        try {
            this.vector = new Vector3D(sensorData.getFloat("xValue"), sensorData.getFloat("yValue"), sensorData.getFloat("zValue"));
            this.timestamp = timestamp;
            this.startTimestamp = startTimestamp;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exports a list of raw inertial data to CSV (to test the parser)
     * @param inertialSensorRecordList a list of raw inertial values
     * @param type the type of sensor which produced the value
     * @param filename the name of the file for the export
     */
    static void toCSV(List<InertialSensorRecord> inertialSensorRecordList, String type, String filename) {
        try {
            String[] HEADERS = {"timestamp", "x " + type + " value", "y " + type + " value", "z " + type + " value"};
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
            CSVPrinter accelerationPrinter = new CSVPrinter(bufferedWriter, CSVFormat.EXCEL.withHeader(HEADERS));
            for (InertialSensorRecord sensorRecord : inertialSensorRecordList) {
                accelerationPrinter.printRecord(sensorRecord.timestamp.getTime() - sensorRecord.startTimestamp.getTime(), sensorRecord.getX(), sensorRecord.getY(), sensorRecord.getZ());
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    double getX() {
        return vector.getX();
    }

    double getY() {
        return vector.getY();
    }

    double getZ() {
        return vector.getZ();
    }

    long getTime() {
        return timestamp.getTime();
    }
}
