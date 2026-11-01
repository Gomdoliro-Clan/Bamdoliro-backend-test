package com.bamdoliro.assessment.service;

import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.repository.InMemoryApplicantRepository;
import com.bamdoliro.assessment.repository.InMemoryApplicationRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ApplicationQueryService {

    private final InMemoryApplicationRepository applicationRepository;
    private final InMemoryApplicantRepository applicantRepository;

    public ApplicationQueryService(InMemoryApplicationRepository applicationRepository,
                                   InMemoryApplicantRepository applicantRepository) {
        this.applicationRepository = applicationRepository;
        this.applicantRepository = applicantRepository;
    }

    /**
     * 관리자 지원자 목록. 전형/상태로 거를 수 있고(null 이면 전체), 접수 순(id 오름차순)으로 페이지네이션한다.
     * page 는 0부터 시작한다.
     */
    public List<ApplicationSummary> list(Track track, ApplicationStatus status, int page, int size) {
        List<Application> applications = applicationRepository.findAll().stream()
                .filter(a -> track == null || a.getTrack() == track)
                .filter(a -> status == null || a.getStatus() == status)
                .sorted(Comparator.comparing(Application::getId))
                .skip((long) page * size)
                .limit(size)
                .toList();

        List<ApplicationSummary> result = new ArrayList<>();
        for (Application application : applications) {
            String name = applicantRepository.findById(application.getApplicantId())
                    .map(a -> a.name())
                    .orElse("(알 수 없음)");
            result.add(new ApplicationSummary(application.getId(), name, application.getTrack(), application.getStatus()));
        }
        return result;
    }
}
