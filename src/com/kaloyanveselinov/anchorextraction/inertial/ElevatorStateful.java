package com.kaloyanveselinov.anchorextraction.inertial;

import org.statefulj.persistence.annotations.State;

public class ElevatorStateful {
    @State
    private String state;

    public String getState() {
        return state;
    }
}
