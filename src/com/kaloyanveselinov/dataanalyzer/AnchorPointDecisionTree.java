package com.kaloyanveselinov.dataanalyzer;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import com.kaloyanveselinov.datacollection.DataSet;

import java.io.File;
import java.util.LinkedList;

public class AnchorPointDecisionTree {
    private LinkedList<AggregatedReading> aggregatedReadingsBuffer = new LinkedList<>();

    public enum AnchorPointType {
        STANDING, WALKING, STAIRS, ELEVATOR, ESCALATOR, UNKNOWN
    }

    private boolean isInElevator() {
        return false;
    }

    public AnchorPointType getAnchorPoint(AggregatedReading newReading) {
        aggregatedReadingsBuffer.add(newReading);
        if (isInElevator()) return AnchorPointType.ELEVATOR;
        else if (newReading.getAccelerationVariance() < 0.1) {
            return AnchorPointType.STANDING;
        } else if (newReading.getAccYZCorrelation() < -0.06) {
            return AnchorPointType.STAIRS;
        } else return AnchorPointType.WALKING;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java DecisionTree filename.JSON");
            System.exit(-1);
        }
        File file = new File(args[0]);
        if (file.exists() && !file.isDirectory()) {
            DataSet dataSet = new DataSet(file);
            AnchorPointDecisionTree tree = new AnchorPointDecisionTree();
            for (AggregatedReading aggregatedReading : dataSet.aggregateReadings(1000)) {
                System.out.println(tree.getAnchorPoint(aggregatedReading));
            }
        } else System.err.println("No such file");
    }
}
