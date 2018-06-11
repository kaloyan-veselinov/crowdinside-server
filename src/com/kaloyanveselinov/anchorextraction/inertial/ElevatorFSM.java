package com.kaloyanveselinov.anchorextraction.inertial;

import org.statefulj.fsm.FSM;
import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.State;
import org.statefulj.fsm.model.impl.StateImpl;
import org.statefulj.persistence.memory.MemoryPersisterImpl;

import java.util.LinkedList;
import java.util.List;

class ElevatorFSM extends FSM<Elevator> {

    // Events
    static String notchUp = "Notch up";
    static String notchDown = "Notch down";
    static String silence = "Silence event";

    // States
    static State<Elevator> standing = new StateImpl<>("Standing");
    static State<Elevator> overWeight = new StateImpl<>("Over-weight");
    static State<Elevator> weightLoss = new StateImpl<>("Weight-loss");
    static State<Elevator> goingUp = new StateImpl<>("Going up");
    static State<Elevator> goingDown = new StateImpl<>("Going down");
    static State<Elevator> elevatorUp = new StateImpl<>("Elevator up", true);
    static State<Elevator> elevatorDown = new StateImpl<>("Elevator down", true);
    private static final State<Elevator> START_STATE = standing;

    // Action builder
    public static class ElevatorAction<Elevator> implements Action<Elevator> {
        String action;

        ElevatorAction(String action){
            this.action = action;
        }

        public void execute(Elevator stateful, String event, Object ... args){
            System.out.println(action);
        }
    }

    // Actions
    private static ElevatorAction<Elevator> inElevatorUp = new ElevatorAction<>("In elevator, going up");
    private static ElevatorAction<Elevator> inElevatorDown =  new ElevatorAction<>("In elevator, going down");

    private static List<State<Elevator>> getStates(){
        List<State<Elevator>> states = new LinkedList<>();
        states.add(standing);
        states.add(goingUp);
        states.add(goingDown);
        states.add(overWeight);
        states.add(weightLoss);
        states.add(elevatorUp);
        states.add(elevatorDown);
        return states;
    }

    // Transitions
    private void initTransitions() {
        standing.addTransition(notchUp, overWeight, null);
        overWeight.addTransition(silence, goingUp, null);
        overWeight.addTransition(notchDown, START_STATE, null);
        goingUp.addTransition(notchDown, elevatorUp, inElevatorUp);
        goingUp.addTransition(notchUp, START_STATE, null);
        elevatorUp.addTransition(notchUp, START_STATE, null);
        elevatorUp.addTransition(notchDown, START_STATE, null);
        elevatorUp.addTransition(silence, START_STATE, null);

        standing.addTransition(notchDown, weightLoss, null);
        weightLoss.addTransition(silence, goingDown, null);
        weightLoss.addTransition(notchUp, START_STATE, null);
        goingDown.addTransition(notchUp, elevatorDown, inElevatorDown);
        goingDown.addTransition(notchDown, START_STATE, null);
        elevatorDown.addTransition(notchUp, START_STATE, null);
        elevatorDown.addTransition(notchDown, START_STATE, null);
        elevatorDown.addTransition(silence, START_STATE, null);
    }

    ElevatorFSM() {
        super("Elevator FSM", new MemoryPersisterImpl<>(getStates(), START_STATE));
        initTransitions();
    }
}
