package com.bamdoliro.assessment.problems;

import java.util.List;

/** 두 목록 모두 심사 순서(점수 내림차순, 동점이면 접수번호 오름차순)를 유지한다. */
public record SelectionResult(List<Candidate> passed, List<Candidate> failed) {
}
