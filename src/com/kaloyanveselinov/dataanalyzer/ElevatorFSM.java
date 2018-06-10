package com.kaloyanveselinov.dataanalyzer;

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
    static String reset = "Reset";

    // States
    static State<Elevator> standing = new StateImpl<>("Standing");
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

    // Transitions
    private void initTransitions(){
        standing.addTransition(notchUp, goingUp, null);
        standing.addTransition(notchDown, goingDown, null);
        goingUp.addTransition(notchDown, elevatorUp, inElevatorUp);
        goingUp.addTransition(reset, START_STATE, null);
        goingDown.addTransition(notchUp, elevatorDown, inElevatorDown);
        goingDown.addTransition(reset, START_STATE);
        elevatorUp.addTransition(reset, START_STATE);
        elevatorDown.addTransition(reset, START_STATE);
    }

    private static List<State<Elevator>> getStates(){
        List<State<Elevator>> states = new LinkedList<>();
        states.add(standing);
        states.add(goingUp);
        states.add(goingDown);
        states.add(elevatorUp);
        states.add(elevatorDown);
        return states;
    }

    ElevatorFSM() {
        super("Elevator FSM", new MemoryPersisterImpl<>(getStates(), START_STATE));
        initTransitions();
    }
}
