package com.bamdoliro.assessment.service;

import com.bamdoliro.assessment.domain.Track;

import java.util.concurrent.ConcurrentHashMap;

/** 전형별 접수번호를 발급한다. 형식은 전형 코드 1글자 + 4자리 일련번호 (예: M0001). */
public class ApplicantNumberIssuer {

    private final ConcurrentHashMap<Track, Integer> counters = new ConcurrentHashMap<>();

    public String issue(Track track) {
        int current = counters.getOrDefault(track, 0);
        simulateSequenceStoreLatency();
        int next = current + 1;
        counters.put(track, next);
        return track.getCode() + String.format("%04d", next);
    }

    private void simulateSequenceStoreLatency() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
