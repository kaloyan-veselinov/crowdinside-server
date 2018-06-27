/**
 * A step detector as described in Uptime
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 * @see <a href="http://ieeexplore.ieee.org/abstract/document/6214359/"></a>
 */

package com.kaloyanveselinov.tracesgeneration.stepdetector;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import com.kaloyanveselinov.datacollection.DataSet;
import org.statefulj.fsm.TooBusyException;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

public class StepDetector {
    // Stateful entity for the StepFSM as per Statefulj's specifications
    private StepStateful stepStateful = new StepStateful();

    // The Statefulj step FSM
    private StepFSM fsm = new StepFSM(this);

    // A buffer for all the readings in a step
    private LinkedList<AggregatedReading> buffer = new LinkedList<>();

    // A list with all detected steps and their data
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

    /**
     * Updates the FSM on the arrival of a new reading
     *
     * @param reading the new reading
     * @throws TooBusyException Shows that an error has occurred in Statefulj (concurrent calls...)
     */
    private void onNewReading(AggregatedReading reading) throws TooBusyException {
        buffer.add(reading);
        fsm.updateStateOnReading(stepStateful, reading);
    }

    /**
     * Filters all the readings between the step's start and end times
     * @return the <code>AggregatedReadings</code> corresponding to the step
     */
    private LinkedList<AggregatedReading> getStepReading(){
        Iterator<AggregatedReading> it = buffer.descendingIterator();
        LinkedList<AggregatedReading> stepReadings = new LinkedList<>();
        AggregatedReading next;
        while(it.hasNext() && !(next = it.next()).getTimestamp().equals(stepStateful.getStartTime())){
            stepReadings.add(next);
        }
        return stepReadings;
    }

    /**
     * Callback function on step detection
     */
    void onStepDetected() {
        steps.add(new Step(getStepReading()));
        buffer.clear();
    }
}
