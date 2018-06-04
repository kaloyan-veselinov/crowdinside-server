package com.kaloyanveselinov.dataanalyzer;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import com.kaloyanveselinov.datacollection.DataSet;

import java.io.File;
import java.util.LinkedList;

public class AnchorPointDecisionTree {
    private static final double ACCELERATION_VARIANCE_THRESHOLD = 0.5;
    private static final double ACCELERATION_CORRELATION_THRESHOLD = 0.5;
    private static final double MAGNETOMETER_VARIANCE_THRESHOLD = 0.6;

    private LinkedList<AggregatedReading> aggregatedReadingsBuffer = new LinkedList<>();

    public enum AnchorPointType{
        STANDING, WALKING, STAIRS, ELEVATOR, ESCALATOR, UNKNOWN
    }

    private boolean isInElevator(){return false;}

    public AnchorPointType getAnchorPoint(AggregatedReading newReading){
        aggregatedReadingsBuffer.add(newReading);
        if(isInElevator()) return AnchorPointType.ELEVATOR;
        else if(newReading.getAccelerationVariance() < ACCELERATION_VARIANCE_THRESHOLD){
            if(newReading.getMagnetometerVariance() < MAGNETOMETER_VARIANCE_THRESHOLD) return AnchorPointType.STANDING;
            else return AnchorPointType.ESCALATOR;
        } else if(newReading.getAccYZCorrelation() > ACCELERATION_CORRELATION_THRESHOLD){
            return AnchorPointType.STAIRS;
        } else return AnchorPointType.WALKING;
    }

    public static void main(String[] args) {
        if (args.length != 1){
            System.err.println("Usage: java DecisionTree filename.JSON");
            System.exit(-1);
        }
        File file = new File(args[0]);
        if(file.exists() && !file.isDirectory()) {
            DataSet dataSet = new DataSet(file, "", 500);
            AnchorPointDecisionTree tree = new AnchorPointDecisionTree();
            for (AggregatedReading aggregatedReading : dataSet.getAggregatedReadings()) {
                System.out.println(tree.getAnchorPoint(aggregatedReading));
            }
        } else System.err.println("No such file");
    }
}
