package com.bamdoliro.assessment.repository;

import com.bamdoliro.assessment.domain.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DB 를 흉내 낸 인메모리 저장소.
 * 실제 DB 처럼 조회/저장 시 값을 복사해서 주고받는다. (조회한 객체를 고쳐도 save 하기 전에는 저장소에 반영되지 않는다)
 * queryCount 는 "쿼리가 몇 번 나갔는지" 를 세는 용도이다.
 */
public class InMemoryApplicationRepository {

    private final Map<Long, Application> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final AtomicInteger queryCount = new AtomicInteger();

    public Application save(Application application) {
        queryCount.incrementAndGet();
        Application copy = application.copy();
        if (copy.getId() == null) {
            copy.assignId(sequence.incrementAndGet());
        }
        store.put(copy.getId(), copy);
        return copy.copy();
    }

    public Optional<Application> findById(long id) {
        queryCount.incrementAndGet();
        Application found = store.get(id);
        return Optional.ofNullable(found).map(Application::copy);
    }

    public List<Application> findAll() {
        queryCount.incrementAndGet();
        List<Application> result = new ArrayList<>();
        for (Application application : store.values()) {
            result.add(application.copy());
        }
        return result;
    }

    public int queryCount() {
        return queryCount.get();
    }

    public void resetQueryCount() {
        queryCount.set(0);
    }
}
