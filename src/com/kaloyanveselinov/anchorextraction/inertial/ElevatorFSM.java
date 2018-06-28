package com.kaloyanveselinov.anchorextraction.inertial;

import com.kaloyanveselinov.datacollection.AggregatedReading;
import org.statefulj.fsm.FSM;
import org.statefulj.fsm.TooBusyException;
import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.State;
import org.statefulj.fsm.model.impl.StateImpl;
import org.statefulj.persistence.memory.MemoryPersisterImpl;

import java.util.LinkedList;
import java.util.List;

class ElevatorFSM extends FSM<ElevatorStateful> {

    // Events
    static String notchUp = "Notch up";
    static String notchDown = "Notch down";
    static String silence = "Silence event";

    // States
    private static State<ElevatorStateful> standing = new StateImpl<>("Standing");
    private static State<ElevatorStateful> overWeight = new StateImpl<>("Over-weight");
    private static State<ElevatorStateful> weightLoss = new StateImpl<>("Weight-loss");
    private static State<ElevatorStateful> goingUp = new StateImpl<>("Going up");
    private static State<ElevatorStateful> goingDown = new StateImpl<>("Going down");
    static State<ElevatorStateful> elevatorUp = new StateImpl<>("ElevatorStateful up", true);
    static State<ElevatorStateful> elevatorDown = new StateImpl<>("ElevatorStateful down", true);
    private static final State<ElevatorStateful> START_STATE = standing;

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
    private static Action<ElevatorStateful> inElevatorUp = new ElevatorAction<>("In elevator, going up");
    private static Action<ElevatorStateful> inElevatorDown =  new ElevatorAction<>("In elevator, going down");

    private static List<State<ElevatorStateful>> getStates(){
        List<State<ElevatorStateful>> states = new LinkedList<>();
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
        super("ElevatorStateful FSM", new MemoryPersisterImpl<>(getStates(), START_STATE));
        initTransitions();
    }

    boolean isInElevator(ElevatorStateful elevatorStateful, AggregatedReading reading) throws TooBusyException {
        double accMagn = reading.getAccelerationMagnitude();
        if (accMagn < 9.2)
            onEvent(elevatorStateful, ElevatorFSM.notchDown);
        else if (accMagn > 10.4)
            onEvent(elevatorStateful, ElevatorFSM.notchUp);
        else if (accMagn > 9.7 && accMagn < 9.95)
            onEvent(elevatorStateful, ElevatorFSM.silence);
        return ElevatorFSM.elevatorDown.getName().equals(elevatorStateful.getState()) || ElevatorFSM.elevatorUp.getName().equals(elevatorStateful.getState());
    }
}
