package com.kaloyanveselinov.tracesgeneration.stepdetector;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import com.kaloyanveselinov.datacollection.DataSet;
import org.statefulj.fsm.TooBusyException;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

public class StepDetector {
    private StepStateful stepStateful = new StepStateful();
    private StepFSM fsm = new StepFSM(this);
    private LinkedList<AggregatedReading> buffer = new LinkedList<>();
    private LinkedList<Step> steps = new LinkedList<>();

    public static void main(String[] args) throws TooBusyException {
        if (args.length != 2) {
            System.err.println("Usage: java DecisionTree filename.JSON gait");
            System.exit(-1);
        }
        File file = new File(args[0]);
        if (file.exists() && !file.isDirectory()) {
            DataSet dataSet = new DataSet(file);
            StepDetector detector = new StepDetector();
            for(AggregatedReading reading: dataSet.aggregateReadings(100))
                detector.onNewReading(reading);
            System.out.println(detector.steps.size() + " steps detected");
            String fileSuffix = "_" + dataSet.getTimestamp().toString().substring(0, dataSet.getTimestamp().toString().length() - 4).replaceAll(":", "-").replaceAll(" ", "_") + ".csv";
            Step.toCSV(detector.steps, args[1] + fileSuffix, args[1]);
        } else System.err.println("No such file");
    }

    private void onNewReading(AggregatedReading reading) throws TooBusyException {
        buffer.add(reading);
        fsm.updateStateOnReading(stepStateful, reading);
    }

    private LinkedList<AggregatedReading> getStepReading(){
        Iterator<AggregatedReading> it = buffer.descendingIterator();
        LinkedList<AggregatedReading> stepReadings = new LinkedList<>();
        AggregatedReading next;
        while(it.hasNext() && !(next = it.next()).getTimestamp().equals(stepStateful.getStartTime())){
            stepReadings.add(next);
        }
        return stepReadings;
    }

    void onStepDetected() {
        steps.add(new Step(getStepReading()));
        buffer.clear();
    }
}
