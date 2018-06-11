package com.kaloyanveselinov.tracesgeneration.stepdetector;

import org.statefulj.fsm.FSM;
import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.State;
import org.statefulj.fsm.model.impl.StateImpl;
import org.statefulj.persistence.memory.MemoryPersisterImpl;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

class StepFSM extends FSM<Step> {

    // Thresholds
    static final double THR = 0.6;
    static final double POS_PEEK_THR = 1.8;
    static final double NEG_PEEK_THR = -1;
    static final double NEG_THR = -0.6;


    // Events
    private static final String ltThr = "Input < Thr";
    private static final String htThr = "Input > Thr";
    private static final String htNegThr = "Input > Neg_Thr";
    private static final String htPosPeekThr = "Input > Pos_Peek_Thr";
    private static final String ltNegPeekThr = "Input < Neg_Peek_Thr";
    private static final String htNegPeekThr = "Input > Neg_Peek_Thr";

    // States
    private static final State<Step> s0 = new StateImpl<>("S0");
    private static final State<Step> s1 = new StateImpl<>("S1");
    private static final State<Step> s2 = new StateImpl<>("S2");
    private static final State<Step> s3 = new StateImpl<>("S3");
    private static final State<Step> s4 = new StateImpl<>("S4");
    private static final State<Step> s5 = new StateImpl<>("S5");
    private static final State<Step> s6 = new StateImpl<>("S6", true);
    // Actions
    Action<Step> startStep = new Action<Step>() {
        @Override
        public void execute(Step step, String s, Object... objects) {
            step.setStartTime((Timestamp) objects[0]);
            System.out.println("Starting step at " + step.getStartTime());
        }
    };
    Action<Step> endStep = new Action<Step>() {
        @Override
        public void execute(Step step, String s, Object... objects) {
            step.setEndTime((Timestamp) objects[0]);
            System.out.println("Ending step at " + step.getEndTime());
        }
    };

    StepFSM() {
        super("Step FSM", new MemoryPersisterImpl<>(getStates(), s0));
        initTransitions();
    }

    private static List<State<Step>> getStates() {
        List<State<Step>> states = new LinkedList<>();
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
}
