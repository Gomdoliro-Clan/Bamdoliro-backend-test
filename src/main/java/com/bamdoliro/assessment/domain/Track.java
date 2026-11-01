package com.bamdoliro.assessment.domain;

public enum Track {
    MEISTER_TALENT("M"),
    GENERAL("G"),
    SOCIAL_INTEGRATION("S");

    private final String code;

    Track(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
