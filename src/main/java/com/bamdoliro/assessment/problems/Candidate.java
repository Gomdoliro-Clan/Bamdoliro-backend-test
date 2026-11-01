package com.bamdoliro.assessment.problems;

/** 1차 선발 대상 지원자. busan 은 출신 중학교가 부산 소재인지 여부. */
public record Candidate(String applicantNumber, double score, boolean busan) {
}
