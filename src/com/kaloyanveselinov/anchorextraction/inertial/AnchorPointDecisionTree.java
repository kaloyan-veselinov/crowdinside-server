package com.kaloyanveselinov.anchorextraction.inertial;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import com.kaloyanveselinov.datacollection.DataSet;
import org.statefulj.fsm.TooBusyException;

import java.io.File;

public class AnchorPointDecisionTree {
    private Elevator elevator = new Elevator();
    private ElevatorFSM fsm = new ElevatorFSM();

    public static void main(String[] args) throws TooBusyException {
        if (args.length != 1) {
            System.err.println("Usage: java DecisionTree filename.JSON");
            System.exit(-1);
        }
        File file = new File(args[0]);
        if (file.exists() && !file.isDirectory()) {
            DataSet dataSet = new DataSet(file);
            AnchorPointDecisionTree tree = new AnchorPointDecisionTree();
            for (AggregatedReading aggregatedReading : dataSet.aggregateReadings(500)) {
                System.out.println(tree.getAnchorPoint(aggregatedReading));
            }
        } else System.err.println("No such file");
    }

    private AnchorPointType getAnchorPoint(AggregatedReading newReading) throws TooBusyException {
        if (fsm.isInElevator(elevator, newReading)) return AnchorPointType.ELEVATOR;
        else if (newReading.getAccelerationVariance() < 0.1) {
            return AnchorPointType.STANDING;
        } else if (newReading.getAccYZCorrelation() < -0.06) {
            return AnchorPointType.STAIRS;
        } else return AnchorPointType.WALKING;
    }

    public enum AnchorPointType {
        STANDING, WALKING, STAIRS, ELEVATOR
    }
}
