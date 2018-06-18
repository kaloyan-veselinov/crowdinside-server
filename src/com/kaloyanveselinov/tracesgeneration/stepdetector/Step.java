package com.kaloyanveselinov.tracesgeneration.stepdetector;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

class Step {
    private long stepDuration;
    private Timestamp timestamp;
    private DescriptiveStatistics stat = new DescriptiveStatistics();


    Step(LinkedList<AggregatedReading> stepReadings){
        stepDuration = stepReadings.getFirst().getTimestamp().getTime() - stepReadings.getLast().getTimestamp().getTime();
        timestamp = stepReadings.getLast().getTimestamp();
        for (AggregatedReading reading: stepReadings)
            stat.addValue(reading.getAccelerationMagnitude());
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
