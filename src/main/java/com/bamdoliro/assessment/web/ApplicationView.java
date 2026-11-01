package com.bamdoliro.assessment.web;

import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;

public record ApplicationView(long id, Track track, ApplicationStatus status, String photoKey) {

    static ApplicationView from(Application application) {
        return new ApplicationView(application.getId(), application.getTrack(),
                application.getStatus(), application.getPhotoKey());
    }
}
