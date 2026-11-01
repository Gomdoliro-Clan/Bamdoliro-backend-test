package com.bamdoliro.assessment.part1;

import com.bamdoliro.assessment.common.ApiResponse;
import com.bamdoliro.assessment.common.BusinessException;
import com.bamdoliro.assessment.common.ErrorCode;
import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.service.SubmitResult;
import com.bamdoliro.assessment.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** 문제 2. 제출 실패가 성공처럼 보이는 문제 */
class SubmitErrorHandlingTest {

    private final TestFixtures f = new TestFixtures();

    @Test
    @DisplayName("정상 제출하면 접수번호가 발급되고 상태가 SUBMITTED 가 된다")
    void submitSuccess() {
        Application app = f.draft(Track.MEISTER_TALENT);

        SubmitResult result = f.submissionService.submit(app.getId());

        assertEquals("M0001", result.applicantNumber());
        assertEquals(ApplicationStatus.SUBMITTED, f.reload(app).getStatus());
    }

    @Test
    @DisplayName("존재하지 않는 원서를 제출하면 APPLICATION_NOT_FOUND")
    void submitNotFound() {
        BusinessException e = assertThrows(BusinessException.class, () -> f.submissionService.submit(9999L));

        assertEquals(ErrorCode.APPLICATION_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("이미 제출된 원서를 다시 제출하면 ALREADY_SUBMITTED")
    void submitTwiceSequentially() {
        Application app = f.draft(Track.GENERAL);
        f.submissionService.submit(app.getId());

        BusinessException e = assertThrows(BusinessException.class, () -> f.submissionService.submit(app.getId()));

        assertEquals(ErrorCode.ALREADY_SUBMITTED, e.getErrorCode());
    }

    @Test
    @DisplayName("사진 키가 없으면 제출에 실패하고 원서는 임시저장 상태로 남는다")
    void missingPhotoKey() {
        Application app = f.draftWithoutPhoto(Track.GENERAL);

        BusinessException e = assertThrows(BusinessException.class, () -> f.submissionService.submit(app.getId()));

        assertEquals(ErrorCode.MISSING_REQUIRED_FIELD, e.getErrorCode());
        assertEquals(ApplicationStatus.DRAFT, f.reload(app).getStatus());
        assertNull(f.reload(app).getApplicantNumber());
    }

    @Test
    @DisplayName("사진 파일이 저장소에 없으면 제출에 실패한다")
    void missingPhotoFile() {
        Application app = f.draftWithMissingFile(Track.GENERAL);

        BusinessException e = assertThrows(BusinessException.class, () -> f.submissionService.submit(app.getId()));

        assertEquals(ErrorCode.MISSING_REQUIRED_FIELD, e.getErrorCode());
        assertEquals(ApplicationStatus.DRAFT, f.reload(app).getStatus());
    }

    @Test
    @DisplayName("저장소 장애가 나면 예외가 호출자에게 전달되고 원서는 임시저장 상태로 남는다")
    void storageFailure() {
        Application app = f.draft(Track.GENERAL);
        f.photoStorage.failWith(new IllegalStateException("storage down"));

        assertThrows(RuntimeException.class, () -> f.submissionService.submit(app.getId()));

        assertEquals(ApplicationStatus.DRAFT, f.reload(app).getStatus());
    }

    @Test
    @DisplayName("API 응답: 사진이 없으면 400 MISSING_REQUIRED_FIELD (200 이 아니다)")
    void apiResponseOnMissingPhoto() {
        Application app = f.draftWithoutPhoto(Track.GENERAL);

        ApiResponse<SubmitResult> response = f.controller.submit(app.getId());

        assertEquals(400, response.status());
        assertEquals("MISSING_REQUIRED_FIELD", response.code());
    }

    @Test
    @DisplayName("API 응답: 정상 제출은 200")
    void apiResponseOnSuccess() {
        Application app = f.draft(Track.GENERAL);

        ApiResponse<SubmitResult> response = f.controller.submit(app.getId());

        assertEquals(200, response.status());
        assertEquals("G0001", response.data().applicantNumber());
    }
}
