package com.kaloyanveselinov.anchorextraction.inertial;

import org.statefulj.fsm.FSM;
import org.statefulj.fsm.TooBusyException;

public class ElevatorFSMTest {
    private static FSM<ElevatorStateful> fsm = new ElevatorFSM();

    private static boolean testElevatorUp() throws TooBusyException {
        ElevatorStateful elevatorStateful = new ElevatorStateful();
        fsm.onEvent(elevatorStateful, ElevatorFSM.notchUp);
        fsm.onEvent(elevatorStateful, ElevatorFSM.silence);
        fsm.onEvent(elevatorStateful, ElevatorFSM.notchDown);
        return elevatorStateful.getState().equals(ElevatorFSM.elevatorUp.getName());
    }

    private static boolean testElevatorDown() throws TooBusyException {
        ElevatorStateful elevatorStateful = new ElevatorStateful();
        fsm.onEvent(elevatorStateful, ElevatorFSM.notchDown);
        fsm.onEvent(elevatorStateful, ElevatorFSM.silence);
        fsm.onEvent(elevatorStateful, ElevatorFSM.notchUp);
        return elevatorStateful.getState().equals(ElevatorFSM.elevatorDown.getName());
    }

    public static void main(String[] args) throws TooBusyException {
        System.out.println(testElevatorUp());
        System.out.println(testElevatorDown());
    }
}