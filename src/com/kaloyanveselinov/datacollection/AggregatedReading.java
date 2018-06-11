package com.kaloyanveselinov.datacollection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AggregatedReading {
    private Timestamp timestamp;
    private Timestamp startTimestamp;
    private Stat acceleration;
    private Stat magnetometer;
    private Stat gyroscope;

    AggregatedReading(Timestamp startTimestamp, Timestamp timestamp) {
        this.startTimestamp = startTimestamp;
        this.timestamp = timestamp;
    }

    private static String[] buildHeaders(String[] params) {
        LinkedList<String> headers = new LinkedList<>();
        headers.add("timestamp");
        for (String param : params) {
            headers.add("x axis " + param);
            headers.add("y axis " + param);
            headers.add("z axis " + param);
            headers.add("magnitude " + param);
            headers.add("magnitude slope " + param);
            headers.add("variance " + param);
            headers.add("kurtosis " + param);
            headers.add("skewness " + param);
            headers.add("XY correlation " + param);
            headers.add("XZ correlation " + param);
            headers.add("YZ correlation " + param);
        }
        headers.add("class");
        return headers.toArray(new String[headers.size()]);
    }


    void setAcceleration(Iterator<InertialSensorRecord> it, long maxTime) {
        acceleration = new Stat(it, maxTime);
    }

    void setGyroscope(Iterator<InertialSensorRecord> it, long maxTime) {
        gyroscope = new Stat(it, maxTime);
    }

    void setMagnetometer(Iterator<InertialSensorRecord> it, long maxTime) {
        magnetometer = new Stat(it, maxTime);
    }

    private void printReadingToCSV(CSVPrinter csvPrinter, String type) throws IOException {
        csvPrinter.printRecord(timestamp.getTime() - startTimestamp.getTime(),
                acceleration.meanVector.getX(), acceleration.meanVector.getY(), acceleration.meanVector.getZ(),
                acceleration.meanVector.getNorm(), acceleration.simpleRegression.getSlope(), acceleration.descriptiveStatistics.getVariance(),
                acceleration.descriptiveStatistics.getKurtosis(), acceleration.descriptiveStatistics.getSkewness(),
                acceleration.correlation[0], acceleration.correlation[1], acceleration.correlation[2],
                magnetometer.meanVector.getX(), magnetometer.meanVector.getY(), magnetometer.meanVector.getZ(),
                magnetometer.meanVector.getNorm(), magnetometer.simpleRegression.getSlope(), magnetometer.descriptiveStatistics.getVariance(),
                magnetometer.descriptiveStatistics.getKurtosis(), magnetometer.descriptiveStatistics.getSkewness(),
                magnetometer.correlation[0], magnetometer.correlation[1], magnetometer.correlation[2],
                gyroscope.meanVector.getX(), gyroscope.meanVector.getY(), gyroscope.meanVector.getZ(),
                gyroscope.meanVector.getNorm(), gyroscope.simpleRegression.getSlope(), gyroscope.descriptiveStatistics.getVariance(),
                gyroscope.descriptiveStatistics.getKurtosis(), gyroscope.descriptiveStatistics.getSkewness(),
                gyroscope.correlation[0], gyroscope.correlation[1], gyroscope.correlation[2], type);


    }

    public double getAccelerationMagnitude() {
        return acceleration.meanVector.getNorm();
    }

    static void toCSV(List<AggregatedReading> aggregatedReadings, String filename, String type) {
        BufferedWriter bufferedWriter;
        try {
            String[] params = {"acceleration", "magnetometer", "gyroscope"};
            String[] HEADERS = buildHeaders(params);
            bufferedWriter = new BufferedWriter(new FileWriter("aggregated" + filename));
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.EXCEL.withHeader(HEADERS));
            for (AggregatedReading aggregatedReading : aggregatedReadings) {
                aggregatedReading.printReadingToCSV(csvPrinter, type);
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getAccYZCorrelation() {
        return acceleration.correlation[2];
    }

    public double getAccelerationVariance() {
        return acceleration.descriptiveStatistics.getVariance();
    }

    private class Stat {
        private Vector3D meanVector;
        private DescriptiveStatistics descriptiveStatistics;
        private SimpleRegression simpleRegression;
        private double[] correlation;

        private Stat(Iterator<InertialSensorRecord> it, long maxTime) {
            simpleRegression = new SimpleRegression(false);
            double[][] filteredData = filterValues(it, maxTime);
            Mean mean = new Mean();
            meanVector = new Vector3D(mean.evaluate(filteredData[0]), mean.evaluate(filteredData[1]), mean.evaluate(filteredData[2]));
            correlation = getCorrelation(filteredData);
            descriptiveStatistics = new DescriptiveStatistics(filteredData[3]);
        }

        private double[] getCorrelation(double[][] data) {
            double[] corr = {0, 0, 0};
            try {
                PearsonsCorrelation pc = new PearsonsCorrelation();
                corr[0] = pc.correlation(data[0], data[1]);
                corr[1] = pc.correlation(data[0], data[2]);
                corr[2] = pc.correlation(data[1], data[2]);
            } catch (MathIllegalArgumentException e) {
                corr[0] = 0;
                corr[1] = 0;
                corr[2] = 0;
            }
            return corr;
        }

        private double[][] filterValues(Iterator<InertialSensorRecord> it, long maxTime) {
            LinkedList<Vector3D> values = new LinkedList<>();
            InertialSensorRecord next;
            while (it.hasNext() && (next = it.next()).getTime() < maxTime) {
                Vector3D v = new Vector3D(next.getX(), next.getY(), next.getZ());
                values.add(v);
                simpleRegression.addData(next.getTime(), v.getNorm());
            }
            double[][] filteredValues = new double[5][values.size()];
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
    }


}
