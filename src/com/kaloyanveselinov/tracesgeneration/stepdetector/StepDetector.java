package com.kaloyanveselinov.tracesgeneration.stepdetector;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import com.kaloyanveselinov.datacollection.DataSet;
import org.statefulj.fsm.TooBusyException;

import java.io.File;
import java.util.LinkedList;

public class StepDetector {
    private StepStateful stepStateful = new StepStateful();
    private StepFSM fsm = new StepFSM();

    public static void main(String[] args) throws TooBusyException {
        if (args.length != 1) {
            System.err.println("Usage: java DecisionTree filename.JSON");
            System.exit(-1);
        }
        File file = new File(args[0]);
        if (file.exists() && !file.isDirectory()) {
            DataSet dataSet = new DataSet(file);
            new StepDetector().filterSteps(dataSet.aggregateReadings(100));
        } else System.err.println("No such file");
    }

    private LinkedList<AggregatedReading> filterSteps(LinkedList<AggregatedReading> readings) throws TooBusyException {
        int i = 0;
        for (AggregatedReading reading : readings) {
            if (fsm.updateStateOnReading(stepStateful, reading))
                i++;
        }
        System.out.println(i);
        return new LinkedList<>();
    }
}
