package com.bamdoliro.assessment.domain;

import java.util.EnumSet;
import java.util.Set;

public enum ApplicationStatus {
    DRAFT,
    SUBMITTED,
    FIRST_PASSED,
    REJECTED,
    FINAL_PASSED;

    public boolean canTransitionTo(ApplicationStatus next) {
        return allowedNext().contains(next);
    }

    private Set<ApplicationStatus> allowedNext() {
        return switch (this) {
            case DRAFT -> EnumSet.of(SUBMITTED);
            case SUBMITTED -> EnumSet.of(FIRST_PASSED, REJECTED);
            case FIRST_PASSED -> EnumSet.of(FINAL_PASSED, REJECTED);
            case REJECTED -> EnumSet.of(SUBMITTED);
            case FINAL_PASSED -> EnumSet.noneOf(ApplicationStatus.class);
        };
    }
}
