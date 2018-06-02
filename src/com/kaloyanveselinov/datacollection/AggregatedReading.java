package com.kaloyanveselinov.datacollection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AggregatedReading {
    private Timestamp timestamp;
    private Vector3D acceleration;
    private Vector3D gyroscope;
    private Vector3D magnetometer;
    private double accelerationXYCorrelation = 0;
    private double accelerationVariance;
    private double magnetometerVariance;

    AggregatedReading(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    private static double[][] filterValues(Iterator<InertialSensorRecord> it, long maxTime) {
        LinkedList<Vector3D> values = new LinkedList<>();
        InertialSensorRecord next;
        while (it.hasNext() && (next = it.next()).getTime() < maxTime) {
            values.add(new Vector3D(next.getX(), next.getY(), next.getZ()));
        }
        double[][] filteredValues = new double[4][values.size()];
        int i = 0;
        for (Vector3D vector3D : values) {
            filteredValues[0][i] = vector3D.getX();
            filteredValues[1][i] = vector3D.getY();
            filteredValues[2][i] = vector3D.getZ();
            filteredValues[3][i] = vector3D.getNorm();
            i++;
        }
        return filteredValues;
    }

    void setAcceleration(Iterator<InertialSensorRecord> it, long maxTime) {
        double[][] filteredData = filterValues(it, maxTime);
        Mean mean = new Mean();
        acceleration = new Vector3D(mean.evaluate(filteredData[0]), mean.evaluate(filteredData[1]), mean.evaluate(filteredData[2]));
        try {
            accelerationXYCorrelation = new PearsonsCorrelation().correlation(filteredData[1], filteredData[2]);
        } catch (MathIllegalArgumentException e) {
            accelerationXYCorrelation = 0;
        }
        Variance variance = new Variance();
        accelerationVariance = variance.evaluate(filteredData[3]);
    }

    void setGyroscope(Iterator<InertialSensorRecord> it, long maxTime) {
        double[][] filteredData = filterValues(it, maxTime);
        Mean mean = new Mean();
        gyroscope = new Vector3D(mean.evaluate(filteredData[0]), mean.evaluate(filteredData[1]), mean.evaluate(filteredData[2]));
    }

    void setMagnetometer(Iterator<InertialSensorRecord> it, long maxTime) {
        double[][] filteredData = filterValues(it, maxTime);
        Mean mean = new Mean();
        magnetometer = new Vector3D(mean.evaluate(filteredData[0]), mean.evaluate(filteredData[1]), mean.evaluate(filteredData[2]));
        Variance variance = new Variance();
        magnetometerVariance = variance.evaluate(filteredData[3]);
    }


    private void printReadingToCSV(CSVPrinter csvPrinter) throws IOException {
        csvPrinter.printRecord(timestamp.getTime(),
                acceleration.getX(), acceleration.getY(), acceleration.getZ(), acceleration.getNorm(), accelerationVariance, Math.abs(accelerationXYCorrelation),
                magnetometer.getX(), magnetometer.getY(), magnetometer.getZ(), magnetometer.getNorm(), magnetometerVariance,
                gyroscope.getX(), gyroscope.getY(), gyroscope.getZ());


    }

    static void toCSV(List<AggregatedReading> aggregatedReadings, String filename) {
        BufferedWriter bufferedWriter = null;
        try {
            String[] HEADERS = {"timestamp",
                    "x acceleration", "y acceleration", "z acceleration", "acceleration magnitude", "acceleration variance", "acceleration Z-Y correlation",
                    "x magnetometer", "y magnetometer", "z magnetometer", "magnetometer magnitude", "magnetometer variance",
                    "x gyro", "y gyro", "z gyro"};
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

    public Vector3D getAcceleration() {
        return acceleration;
    }

    public Vector3D getGyroscope() {
        return gyroscope;
    }

    public Vector3D getMagnetometer() {
        return magnetometer;
    }

    public double getAccelerationXYCorrelation() {
        return accelerationXYCorrelation;
    }

    public double getAccelerationVariance() {
        return accelerationVariance;
    }

    public double getMagnetometerVariance() {
        return magnetometerVariance;
    }


}
