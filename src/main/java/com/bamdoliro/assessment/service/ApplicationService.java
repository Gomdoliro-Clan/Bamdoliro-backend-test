package com.bamdoliro.assessment.service;

import com.bamdoliro.assessment.common.BusinessException;
import com.bamdoliro.assessment.common.ErrorCode;
import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.repository.InMemoryApplicationRepository;

public class ApplicationService {

    private final InMemoryApplicationRepository repository;

    public ApplicationService(InMemoryApplicationRepository repository) {
        this.repository = repository;
    }

    public Application createDraft(long applicantId, Track track, String photoKey) {
        return repository.save(Application.create(applicantId, track, photoKey));
    }

    public Application update(long applicationId, Track track, String photoKey) {
        Application application = find(applicationId);
        application.update(track, photoKey);
        return repository.save(application);
    }

    /** 관리자용. 선발 결과 반영 등에 쓰인다. */
    public Application changeStatus(long applicationId, ApplicationStatus next) {
        Application application = find(applicationId);
        application.changeStatus(next);
        return repository.save(application);
    }

    private Application find(long applicationId) {
        return repository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
    }
}
