package com.bamdoliro.assessment.part1;

import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.service.ApplicationSummary;
import com.bamdoliro.assessment.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 문제 5. 목록 조회 */
class ListQueryTest {

    private final TestFixtures f = new TestFixtures();

    @Test
    @DisplayName("전형으로 거를 수 있다")
    void filterByTrack() {
        f.draft(Track.GENERAL);
        f.draft(Track.MEISTER_TALENT);
        f.draft(Track.GENERAL);

        List<ApplicationSummary> result = f.queryService.list(Track.GENERAL, null, 0, 10);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.track() == Track.GENERAL));
    }

    @Test
    @DisplayName("상태로 거를 수 있다")
    void filterByStatus() {
        Application a = f.draft(Track.GENERAL);
        f.draft(Track.GENERAL);
        f.submissionService.submit(a.getId());

        List<ApplicationSummary> result = f.queryService.list(null, ApplicationStatus.SUBMITTED, 0, 10);

        assertEquals(1, result.size());
        assertEquals(a.getId().longValue(), result.get(0).applicationId());
    }

    @Test
    @DisplayName("페이지네이션: 접수 순으로 잘라서 돌려준다")
    void pagination() {
        for (int i = 0; i < 25; i++) {
            f.draft(Track.GENERAL);
        }

        List<ApplicationSummary> second = f.queryService.list(null, null, 1, 10);

        assertEquals(10, second.size());
        assertEquals(11L, second.get(0).applicationId());
        assertEquals(20L, second.get(9).applicationId());
        assertEquals(5, f.queryService.list(null, null, 2, 10).size());
    }

    @Test
    @DisplayName("지원자 이름이 함께 조회된다")
    void includesApplicantName() {
        Application a = f.draft(Track.GENERAL);

        List<ApplicationSummary> result = f.queryService.list(null, null, 0, 10);

        assertEquals("지원자1", result.get(0).applicantName());
        assertEquals(a.getId().longValue(), result.get(0).applicationId());
    }

    @Test
    @DisplayName("100명을 조회해도 쿼리는 3번 이하로 나간다")
    void queryCountDoesNotGrowWithResultSize() {
        for (int i = 0; i < 100; i++) {
            f.draft(Track.GENERAL);
        }
        f.applicationRepository.resetQueryCount();
        f.applicantRepository.resetQueryCount();

        List<ApplicationSummary> result = f.queryService.list(null, null, 0, 100);

        assertEquals(100, result.size());
        assertEquals("지원자100", result.get(99).applicantName());
        int totalQueries = f.applicationRepository.queryCount() + f.applicantRepository.queryCount();
        assertTrue(totalQueries <= 3, "나간 쿼리 수: " + totalQueries);
    }
}
