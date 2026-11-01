package com.bamdoliro.assessment.web;

import com.bamdoliro.assessment.common.ApiExceptionHandler;
import com.bamdoliro.assessment.common.ApiResponse;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.service.ApplicationService;
import com.bamdoliro.assessment.service.SubmissionService;
import com.bamdoliro.assessment.service.SubmitResult;

/**
 * HTTP 컨트롤러를 흉내 낸 클래스. 요청 파라미터는 모두 문자열/원시값으로 들어온다고 가정한다.
 * 예외 처리는 ApiExceptionHandler 가 맡는다.
 */
public class ApplicationController {

    private final ApplicationService applicationService;
    private final SubmissionService submissionService;
    private final ApiExceptionHandler exceptionHandler;

    public ApplicationController(ApplicationService applicationService,
                                 SubmissionService submissionService,
                                 ApiExceptionHandler exceptionHandler) {
        this.applicationService = applicationService;
        this.submissionService = submissionService;
        this.exceptionHandler = exceptionHandler;
    }

    /** POST /applications -> 201 */
    public ApiResponse<ApplicationView> createDraft(long applicantId, String track, String photoKey) {
        return exceptionHandler.handle(201, () ->
                ApplicationView.from(applicationService.createDraft(applicantId, parseTrack(track), photoKey)));
    }

    /** PUT /applications/{id} -> 200 */
    public ApiResponse<ApplicationView> update(long applicationId, String track, String photoKey) {
        return exceptionHandler.handle(200, () ->
                ApplicationView.from(applicationService.update(applicationId, parseTrack(track), photoKey)));
    }

    /** POST /applications/{id}/submit -> 200 */
    public ApiResponse<SubmitResult> submit(long applicationId) {
        return exceptionHandler.handle(200, () -> submissionService.submit(applicationId));
    }

    private Track parseTrack(String track) {
        return Track.valueOf(track);
    }
}
