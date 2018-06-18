package com.kaloyanveselinov.tracesgeneration.stepdetector;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import weka.classifiers.Classifier;
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

class Step {
    private long stepDuration;
    private Timestamp timestamp;
    private DescriptiveStatistics stat = new DescriptiveStatistics();

    private Attribute duration = new Attribute("duration");
    private Attribute accVar = new Attribute("acceleration variance");
    private Attribute accPeek = new Attribute("acceleration peek");
    private Attribute accMaxMinDiff = new Attribute("acceleration max-min");
    private Attribute rms = new Attribute("RMS");
    private Attribute rmsTimesDuration = new Attribute("RMS*duration");
    private Attribute gait = new Attribute("gait", getGaits());
    private Instances instances = new Instances("Gaits", getAttributes(), 0);

    private Classifier cls;

    {
        try {
            cls = (Classifier) weka.core.SerializationHelper.read("res/SMO.model");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Step(LinkedList<AggregatedReading> stepReadings){
        stepDuration = stepReadings.getFirst().getTimestamp().getTime() - stepReadings.getLast().getTimestamp().getTime();
        timestamp = stepReadings.getLast().getTimestamp();
        for (AggregatedReading reading: stepReadings)
            stat.addValue(reading.getAccelerationMagnitude());
    }

    private List<String> getGaits(){
        LinkedList<String> gaits = new LinkedList<>();
        gaits.add("walking");
        gaits.add("running");
        gaits.add("jogging");
        return gaits;
    }

    private ArrayList<Attribute> getAttributes(){
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

    enum Gait {
        WALKING(0.74), JOGGING(1.01), RUNNING(1.70);

        double stepSize;
        Gait(double stepSize){
            this.stepSize = stepSize;
        }
    }

    double getDistance(){
        return getGait().stepSize;
    }

    private Gait getGait(){
        if(stat.getMax() < 18.63) return Gait.WALKING;
        else if(stat.getQuadraticMean() < 23.99) return Gait.JOGGING;
        else return Gait.RUNNING;
    }

    private Instance buildInstance(){
        Instance instance = new DenseInstance(6);
        instance.setValue(duration, stepDuration);
        instance.setValue(accVar, stat.getVariance());
        instance.setValue(accPeek, stat.getMax());
        instance.setValue(accMaxMinDiff, stat.getMax() - stat.getMin());
        instance.setValue(rms, stat.getQuadraticMean());
        instance.setValue(rmsTimesDuration, stat.getQuadraticMean() * stepDuration);
        return instance;
    }

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

    static void toCSV(List<Step> steps, String filename, String referenceGait){
        BufferedWriter bw;
        try {
            String[] headers = {"timestamp", "duration", "acceleration variance", "acceleration peek", "acceleration max-min", "RMS", "RMS*duration", "gait"};
            bw = new BufferedWriter(new FileWriter(filename));
            CSVPrinter printer = new CSVPrinter(bw, CSVFormat.EXCEL.withHeader(headers));
            for (Step step: steps) {
                step.printStep(printer, referenceGait);
            }
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
