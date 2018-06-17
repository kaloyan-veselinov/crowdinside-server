package com.kaloyanveselinov.tracesgeneration.stepdetector;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.LinkedList;

class Step {
    private long stepDuration;
    private DescriptiveStatistics stat = new DescriptiveStatistics();

    Step(LinkedList<AggregatedReading> stepReadings){
        stepDuration = stepReadings.getFirst().getTimestamp().getTime() - stepReadings.getLast().getTimestamp().getTime();
        for (AggregatedReading reading: stepReadings)
            stat.addValue(reading.getAccelerationMagnitude());
        printStep();
    }

    void printStep(){
        System.out.println("Step " + this);
        System.out.println(stepDuration);
        System.out.println(stat.getVariance());
        System.out.println(stat.getMax());
        System.out.println(stat.getMax() - stat.getMin());
        System.out.println(stat.getQuadraticMean());
        System.out.println(stat.getQuadraticMean() * stepDuration);
    }

}
