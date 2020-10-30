package com.telluur.SlapBot.features.slapevents.jpa;


import com.telluur.SlapBot.SlapBot;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SlapEventRepository {
    private final EntityManagerFactory entityManagerFactory;

    public SlapEventRepository(SlapBot slapBot) {
        this.entityManagerFactory = slapBot.getEntityManagerFactory();
    }

    public synchronized List<SlapEvent> getEvents() {
        EntityManager em = entityManagerFactory.createEntityManager();
        List<SlapEvent> events = em.createNamedQuery("SlapEvent.findAllEvents", SlapEvent.class).getResultList();
        em.close();
        return events;
    }

    public synchronized List<SlapEvent> getFutureEvents() {
        EntityManager em = entityManagerFactory.createEntityManager();
        List<SlapEvent> events = em.createNamedQuery("SlapEvent.findFutureEventsOrderedByStart", SlapEvent.class).getResultList();
        em.close();
        return events;
    }

    /**
     * Gives back a event object
     *
     * @param ID event ID
     * @return Optional of Event object, empty when ID does not exist
     */
    public synchronized Optional<SlapEvent> getEventByID(String ID) throws IllegalArgumentException {
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            SlapEvent event = em.createNamedQuery("SlapEvent.findEventById", SlapEvent.class)
                    .setParameter("id", ID)
                    .getSingleResult();
            em.close();
            return Optional.of(event);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public synchronized Optional<SlapEvent> getNextEvent() {
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            SlapEvent event = em.createNamedQuery("SlapEvent.findFutureEventsOrderedByStart", SlapEvent.class)
                    .setMaxResults(1)
                    .getSingleResult();
            em.close();
            return Optional.of(event);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * (Over)writes an event object to the internal state
     *
     * @param id    the discord group snowflake id
     * @param event the game object
     * @throws IOException
     */
    public synchronized Optional<SlapEvent> saveEvent(SlapEvent event) {
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            em.close();
            return Optional.of(event);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Deletes an event from storage
     *
     * @param event event to be deleted
     * @throws IOException
     */
    public synchronized void deleteEventByID(SlapEvent event) throws IOException {
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            em.getTransaction().begin();
            em.remove(event);
            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Expanded Functionality
    These methods implement further user facing functionality
    These methods are synchronized as the commands are executed async
     */

    /**
     * Of all events, lists those that are
     * - have a start and end
     * - end date is in the future
     * - ordered by start state
     *
     * @return ordered events by start date
     */
    public synchronized List<SlapEvent> getValidFutureEventsOrderedByStart() {
        return null;
    }

}
