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

class StepFSM extends FSM<StepStateful> {

    // Thresholds
    private static final double THR = 11;
    private static final double POS_PEEK_THR = 12;
    private static final double NEG_PEEK_THR = 8.4;
    private static final double NEG_THR = 9;

    // Events
    private static final String ltThr = "Input < Thr";
    private static final String htThr = "Input > Thr";
    private static final String htNegThr = "Input > Neg_Thr";
    private static final String htPosPeekThr = "Input > Pos_Peek_Thr";
    private static final String ltNegPeekThr = "Input < Neg_Peek_Thr";
    private static final String htNegPeekThr = "Input > Neg_Peek_Thr";

    // States
    private static final State<StepStateful> s0 = new StateImpl<>("S0");
    private static final State<StepStateful> s1 = new StateImpl<>("S1");
    private static final State<StepStateful> s2 = new StateImpl<>("S2");
    private static final State<StepStateful> s3 = new StateImpl<>("S3");
    private static final State<StepStateful> s4 = new StateImpl<>("S4");
    private static final State<StepStateful> s5 = new StateImpl<>("S5");
    private static final State<StepStateful> s6 = new StateImpl<>("S6", true);
    // Actions
    private Action<StepStateful> startStep = (stepStateful, s, objects) -> {
        stepStateful.setStartTime((Timestamp) objects[0]);
        System.out.println("Starting stepStateful at " + stepStateful.getStartTime());
    };
    private Action<StepStateful> endStep = (stepStateful, s, objects) -> {
        stepStateful.setEndTime((Timestamp) objects[0]);
        System.out.println("Ending stepStateful at " + stepStateful.getEndTime());
    };

    StepFSM() {
        super("StepStateful FSM", new MemoryPersisterImpl<>(getStates(), s0));
        initTransitions();
    }

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

    // Transitions
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

    boolean updateStateOnReading(StepStateful stepStateful, AggregatedReading aggregatedReading) throws TooBusyException {
        double accMagn = aggregatedReading.getAccelerationMagnitude();
        Timestamp timestamp = aggregatedReading.getTimestamp();

        if (accMagn > POS_PEEK_THR) onEvent(stepStateful, htPosPeekThr, timestamp);

        if (accMagn > THR) onEvent(stepStateful, htThr, timestamp);
        else if (accMagn <= THR) onEvent(stepStateful, ltThr, timestamp);

        if (accMagn <= NEG_PEEK_THR) onEvent(stepStateful, ltNegPeekThr, timestamp);
        else onEvent(stepStateful, htNegPeekThr, timestamp);

        if (accMagn > NEG_THR) onEvent(stepStateful, htNegThr, timestamp);

        return s6.getName().equals(stepStateful.getState());
    }
}
