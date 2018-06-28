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

/**
 * Class representing a sample of aggregated data extracted from raw data
 *
 * Raw data from the app is averaged in order to group values from different sensors and to reduce the noise
 *
 * @author Kaloyan Veselinov
 * @version 2.0
 */
public class AggregatedReading {
    private Timestamp timestamp;
    private Timestamp startTimestamp; // used to specify the zero time value (first timestamp in the data set)
    private Stat acceleration;
    private Stat magnetometer;
    private Stat gyroscope;

    /**
     * Constructor for an empty AggregatedReading
     * @param startTimestamp the reference time value used to set the 0 time
     * @param timestamp the timestamp for the AggregatedReading
     */
    AggregatedReading(Timestamp startTimestamp, Timestamp timestamp) {
        this.startTimestamp = startTimestamp;
        this.timestamp = timestamp;
    }

    /**
     * Prints a list of AggregatedReadings to a CSV file
     * @param aggregatedReadings the list to export
     * @param filename the name of the output file
     * @param type the user's gait (ground truth for machine learning algorithms)
     */
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

    /**
     * Prints a single AggregatedReading to the provided csvPrinter stream
     * @param csvPrinter the CSV printer to print to
     * @param type the user's gait
     * @throws IOException if an I/O exception has occured during the writing
     */
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

    /**
     * Builds the headers of the CSV file
     * @param params a string with all parameters (acceleration, magnetism, gyro, etc)
     * @return a list containing the headers for the CSV file
     */
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

    /**
     * Aggregates acceleration data until maxTime has been reached
     * @param it an iterator over raw acceleration data
     * @param maxTime the maximum timestamp for this AggregatedReading
     */
    void setAcceleration(Iterator<InertialSensorRecord> it, long maxTime) {
        acceleration = new Stat(it, maxTime);
    }

    /**
     * Aggregates gyroscope data until maxTime has been reached
     * @param it an iterator over raw gyroscope data
     * @param maxTime the maximum timestamp for this AggregatedReading
     */
    void setGyroscope(Iterator<InertialSensorRecord> it, long maxTime) {
        gyroscope = new Stat(it, maxTime);
    }

    /**
     * Aggregates magnetometer data until maxTime has been reached
     * @param it an iterator over raw magnetometer data
     * @param maxTime the maximum timestamp for this AggregatedReading
     */
    void setMagnetometer(Iterator<InertialSensorRecord> it, long maxTime) {
        magnetometer = new Stat(it, maxTime);
    }

    public double getAccYZCorrelation() {
        return acceleration.correlation[2];
    }

    public double getAccelerationVariance() {
        return acceleration.descriptiveStatistics.getVariance();
    }

    public double getAccelerationMagnitude() {
        return acceleration.meanVector.getNorm();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Class for all relevant statistics
     *
     * Uses the Apache Mathematics Commons library
     *
     * @see <a href="http://commons.apache.org/proper/commons-math/"></a>
     */
    private class Stat {
        private Vector3D meanVector;
        private DescriptiveStatistics descriptiveStatistics;
        private SimpleRegression simpleRegression;
        private double[] correlation;

        /**
         * Aggregates values until maxTime has been reached; initializes all relevant statistics
         * @param it an iterator over raw data
         * @param maxTime the maximum timestamp
         */
        private Stat(Iterator<InertialSensorRecord> it, long maxTime) {
            simpleRegression = new SimpleRegression(false);
            double[][] filteredData = filterValues(it, maxTime);
            Mean mean = new Mean();
            meanVector = new Vector3D(mean.evaluate(filteredData[0]), mean.evaluate(filteredData[1]), mean.evaluate(filteredData[2]));
            correlation = getCorrelation(filteredData);
            descriptiveStatistics = new DescriptiveStatistics(filteredData[3]);
        }

        /**
         * Gets the values for the parameter in the current time frame
         * @param it an iterator over raw data
         * @param maxTime the maximum timestamp
         * @return a double array containing values (0 for X axis, 1 for Y axis, 2 for Z axis) in the current time frame
         */
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

        /**
         * Calculates XY, XZ and YZ correlations
         * @param data values for the parameter (0 for x axis, 1 for y axis, 2 for z axis) in the current time frame
         * @return an array with correlations (0 for XY, 1 for XZ and 2 for  YZ)
         */
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
    }


}
