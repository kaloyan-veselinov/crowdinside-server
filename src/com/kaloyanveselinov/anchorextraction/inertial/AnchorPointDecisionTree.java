package com.kaloyanveselinov.anchorextraction.inertial;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import com.kaloyanveselinov.datacollection.DataSet;
import org.statefulj.fsm.TooBusyException;
import java.io.File;

/**
 * Class for detecting inertial anchor points (as described in CrowdInside)
 *
 * Usage: <code>java -jar inertial-anchor.jar dataset.JSON</code>
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 */
public class AnchorPointDecisionTree {
    private ElevatorStateful elevatorStateful = new ElevatorStateful();
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

    /**
     * Detects an anchor points on the arrival of a new reading using a decision tree
     *
     * Parameters created in Weka using a REPTree limited to a depth of 2
     *
     * @param newReading the new reading
     * @return the anchor point type
     * @throws TooBusyException if an error has occured in the ElevatorFSM
     */
    private AnchorPointType getAnchorPoint(AggregatedReading newReading) throws TooBusyException {
        if (fsm.isInElevator(elevatorStateful, newReading)) return AnchorPointType.ELEVATOR;
        else if (newReading.getAccelerationVariance() < 0.1) {
            return AnchorPointType.STANDING;
        } else if (newReading.getAccYZCorrelation() < -0.06) {
            return AnchorPointType.STAIRS;
        } else return AnchorPointType.WALKING;
    }

    /**
     * Possible anchor points
     */
    public enum AnchorPointType {
        STANDING, WALKING, STAIRS, ELEVATOR
    }
}
