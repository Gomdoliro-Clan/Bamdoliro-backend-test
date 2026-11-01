package com.bamdoliro.assessment.support;

import com.bamdoliro.assessment.common.ApiExceptionHandler;
import com.bamdoliro.assessment.domain.Applicant;
import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.repository.InMemoryApplicantRepository;
import com.bamdoliro.assessment.repository.InMemoryApplicationRepository;
import com.bamdoliro.assessment.service.ApplicantNumberIssuer;
import com.bamdoliro.assessment.service.ApplicationQueryService;
import com.bamdoliro.assessment.service.ApplicationService;
import com.bamdoliro.assessment.service.SubmissionService;
import com.bamdoliro.assessment.storage.FakePhotoStorage;
import com.bamdoliro.assessment.web.ApplicationController;

import java.util.concurrent.atomic.AtomicLong;

/** 테스트마다 새로 만드는 객체 묶음. 이 파일은 수정하지 않는다. */
public class TestFixtures {

    public final InMemoryApplicantRepository applicantRepository = new InMemoryApplicantRepository();
    public final InMemoryApplicationRepository applicationRepository = new InMemoryApplicationRepository();
    public final FakePhotoStorage photoStorage = new FakePhotoStorage(10);
    public final ApplicantNumberIssuer numberIssuer = new ApplicantNumberIssuer();

    public final ApplicationService applicationService = new ApplicationService(applicationRepository);
    public final SubmissionService submissionService =
            new SubmissionService(applicationRepository, photoStorage, numberIssuer);
    public final ApplicationQueryService queryService =
            new ApplicationQueryService(applicationRepository, applicantRepository);
    public final ApplicationController controller =
            new ApplicationController(applicationService, submissionService, new ApiExceptionHandler());

    private final AtomicLong counter = new AtomicLong();

    /** 사진까지 올라가 있는 임시저장 원서. */
    public Application draft(Track track) {
        long n = counter.incrementAndGet();
        Applicant applicant = applicantRepository.save("지원자" + n);
        String photoKey = "photo/" + n;
        photoStorage.put(photoKey);
        return applicationRepository.save(Application.create(applicant.id(), track, photoKey));
    }

    /** 사진 키가 비어 있는 임시저장 원서. */
    public Application draftWithoutPhoto(Track track) {
        long n = counter.incrementAndGet();
        Applicant applicant = applicantRepository.save("지원자" + n);
        return applicationRepository.save(Application.create(applicant.id(), track, null));
    }

    /** 사진 키는 있으나 저장소에는 파일이 없는 임시저장 원서. */
    public Application draftWithMissingFile(Track track) {
        long n = counter.incrementAndGet();
        Applicant applicant = applicantRepository.save("지원자" + n);
        return applicationRepository.save(Application.create(applicant.id(), track, "photo/not-uploaded-" + n));
    }

    public Application reload(Application application) {
        return applicationRepository.findById(application.getId()).orElseThrow();
    }
}
