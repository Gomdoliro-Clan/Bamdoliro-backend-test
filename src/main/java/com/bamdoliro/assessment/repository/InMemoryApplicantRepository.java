package com.bamdoliro.assessment.repository;

import com.bamdoliro.assessment.domain.Applicant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryApplicantRepository {

    private final Map<Long, Applicant> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final AtomicInteger queryCount = new AtomicInteger();

    public Applicant save(String name) {
        queryCount.incrementAndGet();
        Applicant applicant = new Applicant(sequence.incrementAndGet(), name);
        store.put(applicant.id(), applicant);
        return applicant;
    }

    public Optional<Applicant> findById(long id) {
        queryCount.incrementAndGet();
        return Optional.ofNullable(store.get(id));
    }

    /** SELECT ... WHERE id IN (...) 에 해당한다. 쿼리는 1번만 나간다. */
    public List<Applicant> findAllByIds(Collection<Long> ids) {
        queryCount.incrementAndGet();
        List<Applicant> result = new ArrayList<>();
        for (Long id : ids) {
            Applicant applicant = store.get(id);
            if (applicant != null) {
                result.add(applicant);
            }
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
