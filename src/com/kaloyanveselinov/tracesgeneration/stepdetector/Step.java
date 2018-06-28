package com.kaloyanveselinov.tracesgeneration.stepdetector;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class Step represents a step detected by the Step FSM with all necessary parameters for classification.
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 */
class Step {
    // Raw step data
    private long stepDuration;
    private Timestamp timestamp;
    private DescriptiveStatistics stat = new DescriptiveStatistics();

    // Classification attributes for Weka API
    private Attribute duration = new Attribute("duration");
    private Attribute accVar = new Attribute("acceleration variance");
    private Attribute accPeek = new Attribute("acceleration peek");
    private Attribute accMaxMinDiff = new Attribute("acceleration max-min");
    private Attribute rms = new Attribute("RMS");
    private Attribute rmsTimesDuration = new Attribute("RMS*duration");
    private Attribute gait = new Attribute("gait", getGaits());
    private Instances instances = new Instances("Gaits", getAttributes(), 1);

    // SMO classifier created by weka and stored in res/SMO.model
    private SMO smo;
    {
        try {
            smo = (SMO) weka.core.SerializationHelper.read(getClass().getClassLoader().getResourceAsStream("SMO.model"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for a step with raw data
     *
     * Extracts classification parameters and determines the user's gait using the <code>SMO classifier</code>.
     * @param stepReadings all the <code>AggregatedReadings</code>in the <code>Step</code>
     */
    Step(LinkedList<AggregatedReading> stepReadings) {
        stepDuration = stepReadings.getFirst().getTimestamp().getTime() - stepReadings.getLast().getTimestamp().getTime();
        timestamp = stepReadings.getLast().getTimestamp();
        for (AggregatedReading reading : stepReadings)
            stat.addValue(reading.getAccelerationMagnitude());
        instances.add(buildInstance());
        try {
            instances.setClass(gait);
            double value = smo.classifyInstance(instances.get(0));
            System.out.println(instances.classAttribute().value((int) value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets possible gaits for the Weka classifier
     * @return a list of all possible gaits
     */
    private List<String> getGaits() {
        LinkedList<String> gaits = new LinkedList<>();
        gaits.add("jogging");
        gaits.add("running");
        gaits.add("walking");
        return gaits;
    }

    /**
     * Sets the Weka classifier attributes
     * @return a list with all classifier attributes
     */
    private ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>(7);
        attributes.add(duration);
        attributes.add(accVar);
        attributes.add(accPeek);
        attributes.add(accMaxMinDiff);
        attributes.add(rms);
        attributes.add(rmsTimesDuration);
        attributes.add(gait);
        return attributes;
    }

    /**
     * Initializes step classification attributes from raw data
     * @return a Weka Instance
     * @see <a href="http://weka.sourceforge.net/doc.dev/weka/core/Instance.html"></a>
     */
    private Instance buildInstance() {
        Instance instance = new DenseInstance(6);
        instance.setValue(duration, stepDuration);
        instance.setValue(accVar, stat.getVariance());
        instance.setValue(accPeek, stat.getMax());
        instance.setValue(accMaxMinDiff, stat.getMax() - stat.getMin());
        instance.setValue(rms, stat.getQuadraticMean());
        instance.setValue(rmsTimesDuration, stat.getQuadraticMean() * stepDuration);
        return instance;
    }

    /**
     * Exports a list of steps to a CSV file
     * @param steps the list of steps to export
     * @param filename the name for the output file
     * @param referenceGait the reference gait (used for labeling the data for Weka classifier training)
     */
    static void toCSV(List<Step> steps, String filename, String referenceGait) {
        BufferedWriter bw;
        try {
            String[] headers = {"timestamp", "duration", "acceleration variance", "acceleration peek", "acceleration max-min", "RMS", "RMS*duration", "gait"};
            bw = new BufferedWriter(new FileWriter(filename));
            CSVPrinter printer = new CSVPrinter(bw, CSVFormat.EXCEL.withHeader(headers));
            for (Step step : steps) {
                step.printStep(printer, referenceGait);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the step to the provided CSVPrinter stream
     * @param printer the CSVPrinter
     * @param referenceGait the reference gait for labeling
     * @throws IOException if an I/O exception has occurred during printing
     */
    private void printStep(CSVPrinter printer, String referenceGait) throws IOException {
        printer.printRecord(timestamp.getTime(),
                stepDuration,
                stat.getVariance(),
                stat.getMax(),
                stat.getMax() - stat.getMin(),
                stat.getQuadraticMean(),
                stat.getQuadraticMean() * stepDuration,
                referenceGait);
    }

}
