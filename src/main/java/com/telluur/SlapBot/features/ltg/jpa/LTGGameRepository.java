package com.telluur.SlapBot.features.ltg.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class LTGGameRepository {
    private final EntityManagerFactory entityManagerFactory;

    public LTGGameRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public boolean hasId(String id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        LTGGame ltgGame = entityManager.find(LTGGame.class, id);
        entityManager.close();
        return ltgGame != null;
    }

    public LTGGame findById(String id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        LTGGame ltgGame = entityManager.find(LTGGame.class, id);
        entityManager.close();
        if (ltgGame != null) {
            return ltgGame;
        } else {
            throw new IllegalArgumentException("Game Snowflake does not exist.");
        }
    }


    public List<String> findAllIds() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<String> ids = entityManager.createNamedQuery("LTGGame.findAllIds", String.class).getResultList();
        entityManager.close();
        return ids;
    }

    public Optional<LTGGame> save(LTGGame ltgGame) throws IOException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(ltgGame);
        entityManager.getTransaction().commit();
        entityManager.close();
        return Optional.of(ltgGame);
    }

    public void deleteById(String id) throws IOException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        LTGGame ltgGame = entityManager.find(LTGGame.class, id);
        if (ltgGame != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(ltgGame);
            entityManager.getTransaction().commit();
            entityManager.close();
        } else {
            entityManager.close();
            throw new IllegalArgumentException("Game Snowflake does not exist.");
        }
    }
}
