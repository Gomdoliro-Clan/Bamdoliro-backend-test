package com.bamdoliro.assessment.domain;

import com.bamdoliro.assessment.common.BusinessException;
import com.bamdoliro.assessment.common.ErrorCode;

public class Application {

    private Long id;
    private final long applicantId;
    private Track track;
    private String photoKey;
    private ApplicationStatus status;
    private String applicantNumber;

    private Application(Long id, long applicantId, Track track, String photoKey,
                        ApplicationStatus status, String applicantNumber) {
        this.id = id;
        this.applicantId = applicantId;
        this.track = track;
        this.photoKey = photoKey;
        this.status = status;
        this.applicantNumber = applicantNumber;
    }

    public static Application create(long applicantId, Track track, String photoKey) {
        return new Application(null, applicantId, track, photoKey, ApplicationStatus.DRAFT, null);
    }

    /** 저장소가 값을 복사해서 주고받는 것을 흉내 내기 위한 복사 메서드. */
    public Application copy() {
        return new Application(id, applicantId, track, photoKey, status, applicantNumber);
    }

    public void assignId(long id) {
        this.id = id;
    }

    public void update(Track track, String photoKey) {
        this.track = track;
        this.photoKey = photoKey;
    }

    public void changeStatus(ApplicationStatus next) {
        this.status = next;
    }

    public void markSubmitted(String applicantNumber) {
        if (this.status != ApplicationStatus.DRAFT) {
            throw new BusinessException(ErrorCode.ALREADY_SUBMITTED);
        }
        this.applicantNumber = applicantNumber;
        this.status = ApplicationStatus.SUBMITTED;
    }

    public Long getId() {
        return id;
    }

    public long getApplicantId() {
        return applicantId;
    }

    public Track getTrack() {
        return track;
    }

    public String getPhotoKey() {
        return photoKey;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public String getApplicantNumber() {
        return applicantNumber;
    }
}
