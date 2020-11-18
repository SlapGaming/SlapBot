package com.telluur.SlapBot.features.ltg.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.Optional;

public class LTGQuickSubscribeRepository {

    private final EntityManagerFactory entityManagerFactory;

    public LTGQuickSubscribeRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Optional<LTGQuickSubscribe> findId(String id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        LTGQuickSubscribe qs = entityManager.find(LTGQuickSubscribe.class, id);
        entityManager.close();
        return qs == null ? Optional.empty() : Optional.of(qs);
    }

    public Optional<LTGQuickSubscribe> save(LTGQuickSubscribe qs) throws IOException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(qs);
        entityManager.getTransaction().commit();
        entityManager.close();
        return Optional.of(qs);
    }
}
