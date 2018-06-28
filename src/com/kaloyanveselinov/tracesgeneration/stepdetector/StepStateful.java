package com.kaloyanveselinov.tracesgeneration.stepdetector;

import org.statefulj.persistence.annotations.State;
import java.sql.Timestamp;

/**
 * Stateful entity for the step detection FSM
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 * @see <a href="http://www.statefulj.org/fsm/#define-your-stateful-entity"></a>
 */
public class StepStateful {
    @State
    private String state;
    private Timestamp startTime;
    private Timestamp endTime;

    /**
     * Gets the step start time
     * @return the step start time
     */
    Timestamp getStartTime() {
        return startTime;
    }

    /**
     * Sets the step start time
     * @param startTime the start time
     */
    void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the step end time
     * @return the step end time
     */
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the currents state of the stateful entity
     * @return the current state
     */
    public String getState() {
        return state;
    }
}
