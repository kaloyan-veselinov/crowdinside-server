package com.kaloyanveselinov.tracesgeneration.stepdetector;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import org.statefulj.fsm.FSM;
import org.statefulj.fsm.TooBusyException;
import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.State;
import org.statefulj.fsm.model.impl.StateImpl;
import org.statefulj.persistence.memory.MemoryPersisterImpl;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

/**
 * The StepFSM class is used to detect a step as described in Uptime
 *
 * The class uses the Statefulj FSM library
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 * @see <a href="http://ieeexplore.ieee.org/abstract/document/6214359/"></a>
 * @see <a href="http://www.statefulj.org/"></a>
 */
class StepFSM extends FSM<StepStateful> {
    private StepDetector detector;

    // Thresholds as defined in UPTIME
    private static final double THR = 10.4;
    private static final double POS_PEEK_THR = 11.6;
    private static final double NEG_PEEK_THR = 8.8;
    private static final double NEG_THR = 9.2;

    // FSM transition events
    private static final String ltThr = "Input < Thr";
    private static final String htThr = "Input > Thr";
    private static final String htNegThr = "Input > Neg_Thr";
    private static final String htPosPeekThr = "Input > Pos_Peek_Thr";
    private static final String ltNegPeekThr = "Input < Neg_Peek_Thr";
    private static final String htNegPeekThr = "Input > Neg_Peek_Thr";

    // FSM states
    private static final State<StepStateful> s0 = new StateImpl<>("S0");
    private static final State<StepStateful> s1 = new StateImpl<>("S1");
    private static final State<StepStateful> s2 = new StateImpl<>("S2");
    private static final State<StepStateful> s3 = new StateImpl<>("S3");
    private static final State<StepStateful> s4 = new StateImpl<>("S4");
    private static final State<StepStateful> s5 = new StateImpl<>("S5");
    private static final State<StepStateful> s6 = new StateImpl<>("S6", true);

    // FSM action when the beginning of a step is detected; sets the step start time
    private Action<StepStateful> startStep = (stepStateful, s, objects) -> {
        stepStateful.setStartTime((Timestamp) objects[0]);
    };
    // FSM action when the end of a step is detected; sets the end time and calls a callback
    private Action<StepStateful> endStep = (stepStateful, s, objects) -> {
        stepStateful.setEndTime((Timestamp) objects[0]);
        detector.onStepDetected();
    };

    /**
     * Constructor for the StepFSM using the provided step detector (for the end step callback)
     * @param detector the StepDetector
     */
    StepFSM(StepDetector detector) {
        super("StepStateful FSM", new MemoryPersisterImpl<>(getStates(), s0));
        initTransitions();
        this.detector = detector;
    }

    /**
     * Initializes the list of possible states as per Statefulj's specifications
     * @return the list of possible states
     */
    private static List<State<StepStateful>> getStates() {
        List<State<StepStateful>> states = new LinkedList<>();
        states.add(s0);
        states.add(s1);
        states.add(s2);
        states.add(s3);
        states.add(s4);
        states.add(s5);
        states.add(s6);
        return states;
    }

    /**
     * Initializes the transitions from one state to another as per Statefulj's specifications
     */
    private void initTransitions() {
        s0.addTransition(htThr, s1, startStep);

        s1.addTransition(htPosPeekThr, s2, null);
        s1.addTransition(ltThr, s4, null);

        s2.addTransition(ltNegPeekThr, s3, null);

        s3.addTransition(htNegPeekThr, s5, null);

        s4.addTransition(ltThr, s0, null);
        s4.addTransition(htThr, s1, null);

        s5.addTransition(ltNegPeekThr, s3, null);
        s5.addTransition(htNegThr, s6, endStep);

        s6.addTransition(ltThr, s0, null);
    }

    /**
     * Updates the state of the StepFSM on the arrival of a new reading
     * @param stepStateful the associated stateful entity to update
     * @param aggregatedReading the new reading
     * @throws TooBusyException if an error has occurred in Statefulj
     */
    void updateStateOnReading(StepStateful stepStateful, AggregatedReading aggregatedReading) throws TooBusyException {
        double accMagn = aggregatedReading.getAccelerationMagnitude();
        Timestamp timestamp = aggregatedReading.getTimestamp();

        if (accMagn > POS_PEEK_THR) onEvent(stepStateful, htPosPeekThr, timestamp);

        if (accMagn > THR) onEvent(stepStateful, htThr, timestamp);
        else if (accMagn <= THR) onEvent(stepStateful, ltThr, timestamp);

        if (accMagn <= NEG_PEEK_THR) onEvent(stepStateful, ltNegPeekThr, timestamp);
        else onEvent(stepStateful, htNegPeekThr, timestamp);

        if (accMagn > NEG_THR) onEvent(stepStateful, htNegThr, timestamp);
    }
}
