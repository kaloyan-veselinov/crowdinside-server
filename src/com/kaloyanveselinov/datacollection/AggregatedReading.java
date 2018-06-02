package com.kaloyanveselinov.datacollection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

public class AggregatedReading {
    private Timestamp timestamp;
    private InertialSensorRecord acceleration;
    private InertialSensorRecord gyroscope;
    private InertialSensorRecord magnetometer;

    public AggregatedReading(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public static InertialSensorRecord aggregateReading(Iterator<InertialSensorRecord> it, long maxTime){
        float xAvg = 0, yAvg = 0, zAvg = 0;
        int nbAggregated = 0;
        InertialSensorRecord next;
        while(it.hasNext() && (next = it.next()).getTime() < maxTime){
            xAvg += next.getX();
            yAvg += next.getY();
            zAvg += next.getZ();
            nbAggregated++;
        }
        if(nbAggregated>0){
            xAvg /= nbAggregated;
            yAvg /= nbAggregated;
            zAvg /= nbAggregated;
        }
        return new InertialSensorRecord(xAvg, yAvg, zAvg);
    }

    public void printReadingToCSV(CSVPrinter csvPrinter) throws IOException {
        csvPrinter.printRecord(timestamp.getTime(), acceleration.getX(), acceleration.getY(), acceleration.getZ(),
                gyroscope.getX(), gyroscope.getY(), gyroscope.getZ(), magnetometer.getX(), magnetometer.getY(),
                magnetometer.getZ());
    }

    public static void toCSV(List<AggregatedReading> aggregatedReadings, String filename) {
        BufferedWriter bufferedWriter = null;
        try {
            String[] HEADERS = {"timestamp", "x acceleration", "y acceleration", "z acceleration",
                    "x gyro", "y gyro", "z gyro",
                    "x magnetometer", "y magnetometer", "z magnetometer"};
            bufferedWriter = new BufferedWriter(new FileWriter("aggregated" + filename));
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.EXCEL.withHeader(HEADERS));
            for (AggregatedReading aggregatedReading : aggregatedReadings) {
                aggregatedReading.printReadingToCSV(csvPrinter);
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAcceleration(InertialSensorRecord acceleration) {
        this.acceleration = acceleration;
    }

    public void setGyroscope(InertialSensorRecord gyroscope) {
        this.gyroscope = gyroscope;
    }

    public void setMagnetometer(InertialSensorRecord magnetometer) {
        this.magnetometer = magnetometer;
    }

}
