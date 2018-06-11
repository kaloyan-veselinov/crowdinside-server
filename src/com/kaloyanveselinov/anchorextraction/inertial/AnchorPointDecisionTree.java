package com.kaloyanveselinov.anchorextraction.inertial;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import com.kaloyanveselinov.datacollection.DataSet;
import org.statefulj.fsm.FSM;
import org.statefulj.fsm.TooBusyException;

import java.io.File;

public class AnchorPointDecisionTree {
    private Elevator elevator = new Elevator();
    private FSM<Elevator> fsm = new ElevatorFSM();

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

    private boolean isInElevator(AggregatedReading reading) throws TooBusyException {
        double accMagn = reading.getAccelerationMagnitude();
        if (accMagn < 9.2)
            fsm.onEvent(elevator, ElevatorFSM.notchDown);
        else if (accMagn > 10.4)
            fsm.onEvent(elevator, ElevatorFSM.notchUp);
        else if (accMagn > 9.7 && accMagn < 9.95)
            fsm.onEvent(elevator, ElevatorFSM.silence);
        return ElevatorFSM.elevatorDown.getName().equals(elevator.getState()) || ElevatorFSM.elevatorUp.getName().equals(elevator.getState());
    }

    private AnchorPointType getAnchorPoint(AggregatedReading newReading) throws TooBusyException {
        if (isInElevator(newReading)) return AnchorPointType.ELEVATOR;
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
