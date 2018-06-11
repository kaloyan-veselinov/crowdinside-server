package com.kaloyanveselinov.tracesgeneration.stepdetector;

import org.statefulj.persistence.annotations.State;

import java.sql.Timestamp;

public class Step {
    @State
    private String state;
    private Timestamp startTime;
    private Timestamp endTime;

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getState() {
        return state;
    }
}
