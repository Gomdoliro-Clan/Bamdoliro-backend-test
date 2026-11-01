package com.bamdoliro.assessment.part1;

import com.bamdoliro.assessment.common.BusinessException;
import com.bamdoliro.assessment.common.ErrorCode;
import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.service.SubmitResult;
import com.bamdoliro.assessment.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 문제 4. 동시 요청 */
class ConcurrentSubmitTest {

    private final TestFixtures f = new TestFixtures();

    /** n 개의 스레드가 동시에 출발해서 각자의 작업을 실행한다. 결과 또는 던진 예외를 순서대로 돌려준다. */
    private static List<Object> runConcurrently(int n, IntFunction<Callable<?>> taskFactory) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(n);
        CyclicBarrier barrier = new CyclicBarrier(n);
        try {
            List<Future<Object>> futures = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                Callable<?> task = taskFactory.apply(i);
                futures.add(pool.submit(() -> {
                    barrier.await();
                    try {
                        return task.call();
                    } catch (Exception e) {
                        return e;
                    }
                }));
            }
            List<Object> results = new ArrayList<>();
            for (Future<Object> future : futures) {
                results.add(future.get(30, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @DisplayName("같은 원서를 동시에 20번 제출해도 정확히 한 번만 성공한다")
    void sameApplicationSubmittedConcurrently() throws Exception {
        Application app = f.draft(Track.MEISTER_TALENT);

        List<Object> results = runConcurrently(20, i -> () -> f.submissionService.submit(app.getId()));

        int success = 0;
        int alreadySubmitted = 0;
        for (Object result : results) {
            if (result instanceof SubmitResult) {
                success++;
            } else if (result instanceof BusinessException e && e.getErrorCode() == ErrorCode.ALREADY_SUBMITTED) {
                alreadySubmitted++;
            }
        }
        assertEquals(1, success, "성공한 요청 수");
        assertEquals(19, alreadySubmitted, "ALREADY_SUBMITTED 로 거절된 요청 수");
        assertEquals(ApplicationStatus.SUBMITTED, f.reload(app).getStatus());

        // 접수번호가 낭비되지 않았는지: 다음 지원자는 M0002 를 받아야 한다.
        Application next = f.draft(Track.MEISTER_TALENT);
        assertEquals("M0002", f.submissionService.submit(next.getId()).applicantNumber());
    }

    @Test
    @DisplayName("서로 다른 지원자 50명이 동시에 제출해도 접수번호는 중복되지 않는다")
    void differentApplicationsSubmittedConcurrently() throws Exception {
        List<Application> apps = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            apps.add(f.draft(Track.GENERAL));
        }

        List<Object> results = runConcurrently(50, i -> () -> f.submissionService.submit(apps.get(i).getId()));

        Set<String> numbers = new HashSet<>();
        for (Object result : results) {
            assertTrue(result instanceof SubmitResult, "예외가 발생했다: " + result);
            numbers.add(((SubmitResult) result).applicantNumber());
        }
        assertEquals(50, numbers.size(), "서로 다른 접수번호의 개수");
        assertTrue(numbers.contains("G0001"));
        assertTrue(numbers.contains("G0050"));
    }

    @Test
    @DisplayName("전형이 다르면 접수번호 일련번호는 서로 독립적이다")
    void numbersAreIndependentPerTrack() {
        Application general = f.draft(Track.GENERAL);
        Application meister = f.draft(Track.MEISTER_TALENT);

        assertEquals("G0001", f.submissionService.submit(general.getId()).applicantNumber());
        assertEquals("M0001", f.submissionService.submit(meister.getId()).applicantNumber());
    }
}
