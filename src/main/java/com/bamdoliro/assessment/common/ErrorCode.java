package com.bamdoliro.assessment.common;

public enum ErrorCode {
    INVALID_TRACK(400),
    MISSING_REQUIRED_FIELD(400),
    APPLICATION_NOT_FOUND(404),
    ALREADY_SUBMITTED(409),
    NOT_EDITABLE(409),
    INVALID_STATUS_TRANSITION(409),
    INTERNAL_SERVER_ERROR(500);

    private final int httpStatus;

    ErrorCode(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
