package com.bamdoliro.assessment.service;

import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;

public record ApplicationSummary(long applicationId, String applicantName, Track track, ApplicationStatus status) {
}
