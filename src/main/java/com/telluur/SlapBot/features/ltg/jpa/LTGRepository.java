package com.telluur.SlapBot.features.ltg.jpa;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class LTGRepository {
    private final EntityManager entityManager;

    public LTGRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean hasId(String id) {
        //Don't use contains here, as we check for ID, not the whole object. (Jack mightve changed the name in DC)
        LTGGame ltgGame = entityManager.find(LTGGame.class, id);
        return ltgGame != null;
    }

    public LTGGame findById(String id) {
        LTGGame ltgGame = entityManager.find(LTGGame.class, id);
        if (ltgGame != null) {
            return ltgGame;
        } else {
            throw new IllegalArgumentException("Game Snowflake does not exist.");
        }
    }


    public List<String> findAllIds() {
        return entityManager.createNamedQuery("LTGGame.findAllIds").getResultList();
    }

    public Optional<LTGGame> save(LTGGame ltgGame) throws IOException {
        entityManager.getTransaction().begin();
        entityManager.persist(ltgGame);
        entityManager.getTransaction().commit();
        return Optional.of(ltgGame);
    }

    public void deleteById(String id) throws IOException {
        LTGGame ltgGame = entityManager.find(LTGGame.class, id);
        if (ltgGame != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(ltgGame);
            entityManager.getTransaction().commit();
        } else {
            throw new IllegalArgumentException("Game Snowflake does not exist.");
        }
    }
}
