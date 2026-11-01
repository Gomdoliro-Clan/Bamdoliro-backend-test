package com.bamdoliro.assessment.part3;

import com.bamdoliro.assessment.problems.Candidate;
import com.bamdoliro.assessment.problems.SelectionAlgorithm;
import com.bamdoliro.assessment.problems.SelectionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 파트 3-1. 공개된 예시 테스트. 채점에는 이 외의 케이스도 사용된다. */
class SelectionAlgorithmTest {

    private static Candidate busan(String number, double score) {
        return new Candidate(number, score, true);
    }

    private static Candidate other(String number, double score) {
        return new Candidate(number, score, false);
    }

    private static List<String> numbers(List<Candidate> candidates) {
        return candidates.stream().map(Candidate::applicantNumber).toList();
    }

    @Test
    @DisplayName("부산 지원자만 있으면 점수 순으로 정원만큼 합격한다")
    void busanOnly() {
        List<Candidate> all = List.of(busan("G0001", 80), busan("G0002", 95), busan("G0003", 70));

        SelectionResult result = SelectionAlgorithm.select(all, 2, 0);

        assertEquals(List.of("G0002", "G0001"), numbers(result.passed()));
        assertEquals(List.of("G0003"), numbers(result.failed()));
    }

    @Test
    @DisplayName("타지역 한도가 차면 타지역 지원자는 탈락하고, 정원은 다음 순위가 채운다")
    void otherRegionLimitSkipsAndFillsWithNext() {
        List<Candidate> all = List.of(
                other("G0001", 99),
                other("G0002", 98),
                busan("G0003", 90),
                busan("G0004", 80));

        SelectionResult result = SelectionAlgorithm.select(all, 3, 1);

        assertEquals(List.of("G0001", "G0003", "G0004"), numbers(result.passed()));
        assertEquals(List.of("G0002"), numbers(result.failed()));
    }

    @Test
    @DisplayName("정원이 찬 뒤에도 정원 순번 지원자와 점수가 같으면 합격한다")
    void tieAtCutoffPasses() {
        List<Candidate> all = List.of(
                busan("G0001", 100),
                busan("G0002", 80),
                busan("G0003", 80),
                busan("G0004", 80),
                busan("G0005", 50));

        SelectionResult result = SelectionAlgorithm.select(all, 2, 0);

        assertEquals(List.of("G0001", "G0002", "G0003", "G0004"), numbers(result.passed()));
        assertEquals(List.of("G0005"), numbers(result.failed()));
    }

    @Test
    @DisplayName("정원이 0 이하이면 전원 불합격")
    void zeroQuota() {
        List<Candidate> all = List.of(busan("G0001", 80), busan("G0002", 70));

        SelectionResult result = SelectionAlgorithm.select(all, 0, 10);

        assertEquals(0, result.passed().size());
        assertEquals(2, result.failed().size());
    }

    @Test
    @DisplayName("정원이 지원자 수보다 크면 전원 합격")
    void quotaLargerThanCandidates() {
        List<Candidate> all = List.of(busan("G0001", 80), busan("G0002", 70));

        SelectionResult result = SelectionAlgorithm.select(all, 10, 0);

        assertEquals(2, result.passed().size());
        assertEquals(0, result.failed().size());
    }

    @Test
    @DisplayName("입력 목록을 수정하지 않는다")
    void doesNotMutateInput() {
        List<Candidate> all = new ArrayList<>(List.of(busan("G0001", 50), busan("G0002", 90)));

        SelectionAlgorithm.select(all, 1, 0);

        assertEquals(List.of("G0001", "G0002"), numbers(all));
    }
}
