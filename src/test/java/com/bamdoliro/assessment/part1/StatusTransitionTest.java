package com.bamdoliro.assessment.part1;

import com.bamdoliro.assessment.common.BusinessException;
import com.bamdoliro.assessment.common.ErrorCode;
import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** 문제 1. 원서 상태 전이 */
class StatusTransitionTest {

    private final TestFixtures f = new TestFixtures();

    private Application submitted() {
        Application app = f.draft(Track.GENERAL);
        f.submissionService.submit(app.getId());
        return f.reload(app);
    }

    @Test
    @DisplayName("임시저장 상태의 원서는 수정할 수 있다")
    void draftCanBeUpdated() {
        Application app = f.draft(Track.GENERAL);

        f.applicationService.update(app.getId(), Track.MEISTER_TALENT, "photo/other");

        assertEquals(Track.MEISTER_TALENT, f.reload(app).getTrack());
    }

    @Test
    @DisplayName("제출된 원서는 수정할 수 없다")
    void submittedCannotBeUpdated() {
        Application app = submitted();

        BusinessException e = assertThrows(BusinessException.class,
                () -> f.applicationService.update(app.getId(), Track.MEISTER_TALENT, "photo/other"));

        assertEquals(ErrorCode.NOT_EDITABLE, e.getErrorCode());
        assertEquals(Track.GENERAL, f.reload(app).getTrack());
    }

    @Test
    @DisplayName("허용된 전이: 제출 -> 1차합격 -> 최종합격")
    void allowedTransitions() {
        Application app = submitted();

        f.applicationService.changeStatus(app.getId(), ApplicationStatus.FIRST_PASSED);
        f.applicationService.changeStatus(app.getId(), ApplicationStatus.FINAL_PASSED);

        assertEquals(ApplicationStatus.FINAL_PASSED, f.reload(app).getStatus());
    }

    @Test
    @DisplayName("임시저장 상태에서 바로 합격 처리할 수 없다")
    void draftCannotJumpToPassed() {
        Application app = f.draft(Track.GENERAL);

        BusinessException e = assertThrows(BusinessException.class,
                () -> f.applicationService.changeStatus(app.getId(), ApplicationStatus.FINAL_PASSED));

        assertEquals(ErrorCode.INVALID_STATUS_TRANSITION, e.getErrorCode());
        assertEquals(ApplicationStatus.DRAFT, f.reload(app).getStatus());
    }

    @Test
    @DisplayName("제출된 원서를 임시저장 상태로 되돌릴 수 없다")
    void submittedCannotGoBackToDraft() {
        Application app = submitted();

        BusinessException e = assertThrows(BusinessException.class,
                () -> f.applicationService.changeStatus(app.getId(), ApplicationStatus.DRAFT));

        assertEquals(ErrorCode.INVALID_STATUS_TRANSITION, e.getErrorCode());
    }

    @Test
    @DisplayName("불합격 처리된 원서는 되살릴 수 없다")
    void rejectedIsTerminal() {
        Application app = submitted();
        f.applicationService.changeStatus(app.getId(), ApplicationStatus.REJECTED);

        BusinessException e = assertThrows(BusinessException.class,
                () -> f.applicationService.changeStatus(app.getId(), ApplicationStatus.SUBMITTED));

        assertEquals(ErrorCode.INVALID_STATUS_TRANSITION, e.getErrorCode());
        assertEquals(ApplicationStatus.REJECTED, f.reload(app).getStatus());
    }
}
