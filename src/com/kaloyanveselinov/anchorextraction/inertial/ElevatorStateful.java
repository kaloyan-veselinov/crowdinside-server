package com.kaloyanveselinov.anchorextraction.inertial;

import org.statefulj.persistence.annotations.State;

/**
 * Stateful object as per Statefulj's specifications
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 * @see <a href="http://www.statefulj.org/fsm/#define-your-stateful-entity"></a>
 */
class ElevatorStateful {
    @State
    private String state;

    String getState() {
        return state;
    }
}
