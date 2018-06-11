package com.kaloyanveselinov.anchorextraction.inertial;

import org.statefulj.fsm.FSM;
import org.statefulj.fsm.TooBusyException;

public class ElevatorFSMTest {
    private static FSM<Elevator> fsm = new ElevatorFSM();

    private static boolean testElevatorUp() throws TooBusyException {
        Elevator elevator = new Elevator();
        fsm.onEvent(elevator, ElevatorFSM.notchUp);
        fsm.onEvent(elevator, ElevatorFSM.silence);
        fsm.onEvent(elevator, ElevatorFSM.notchDown);
        return elevator.getState().equals(ElevatorFSM.elevatorUp.getName());
    }

    private static boolean testElevatorDown() throws TooBusyException {
        Elevator elevator = new Elevator();
        fsm.onEvent(elevator, ElevatorFSM.notchDown);
        fsm.onEvent(elevator, ElevatorFSM.silence);
        fsm.onEvent(elevator, ElevatorFSM.notchUp);
        return elevator.getState().equals(ElevatorFSM.elevatorDown.getName());
    }

    public static void main(String[] args) throws TooBusyException {
        System.out.println(testElevatorUp());
        System.out.println(testElevatorDown());
    }
}