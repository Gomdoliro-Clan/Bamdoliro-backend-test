package com.bamdoliro.assessment.storage;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FakePhotoStorage implements PhotoStorage {

    private final Set<String> keys = ConcurrentHashMap.newKeySet();
    private final long latencyMillis;
    private volatile RuntimeException failure;

    public FakePhotoStorage(long latencyMillis) {
        this.latencyMillis = latencyMillis;
    }

    public void put(String key) {
        keys.add(key);
    }

    /** 이후 exists 호출이 주어진 예외로 실패하게 만든다. (저장소 장애 흉내) */
    public void failWith(RuntimeException failure) {
        this.failure = failure;
    }

    @Override
    public boolean exists(String key) {
        sleep(latencyMillis);
        if (failure != null) {
            throw failure;
        }
        return keys.contains(key);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
