package com.kaloyanveselinov.dataanalyzer;

import org.statefulj.persistence.annotations.State;

public class Elevator {
    @State
    private String state;

    public String getState() {
        return state;
    }
}
