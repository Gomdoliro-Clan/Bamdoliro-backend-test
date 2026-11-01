package com.bamdoliro.assessment.service;

import com.bamdoliro.assessment.common.BusinessException;
import com.bamdoliro.assessment.common.ErrorCode;
import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.repository.InMemoryApplicationRepository;
import com.bamdoliro.assessment.storage.PhotoStorage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SubmissionService {

    private static final Logger log = Logger.getLogger(SubmissionService.class.getName());

    private final InMemoryApplicationRepository repository;
    private final PhotoStorage photoStorage;
    private final ApplicantNumberIssuer numberIssuer;

    public SubmissionService(InMemoryApplicationRepository repository,
                             PhotoStorage photoStorage,
                             ApplicantNumberIssuer numberIssuer) {
        this.repository = repository;
        this.photoStorage = photoStorage;
        this.numberIssuer = numberIssuer;
    }

    /** 원서를 최종 제출한다. 제출이 끝나면 접수번호가 발급된다. */
    public SubmitResult submit(long applicationId) {
        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new BusinessException(ErrorCode.ALREADY_SUBMITTED);
        }

        try {
            validateRequired(application);
            String number = numberIssuer.issue(application.getTrack());
            application.markSubmitted(number);
            repository.save(application);
            return new SubmitResult(application.getId(), number);
        } catch (Exception e) {
            log.log(Level.WARNING, "원서 제출 중 오류가 발생했습니다. id=" + applicationId, e);
            return new SubmitResult(application.getId(), null);
        }
    }

    private void validateRequired(Application application) {
        String photoKey = application.getPhotoKey();
        if (photoKey == null || photoKey.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD);
        }
        if (!photoStorage.exists(photoKey)) {
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD);
        }
    }
}
