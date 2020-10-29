package com.telluur.SlapBot.features.slapevents.jpa;


import com.telluur.SlapBot.SlapBot;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SlapEventRepository {
    private final EntityManager entityManager;

    public SlapEventRepository(SlapBot slapBot) {
        this.entityManager = slapBot.getEntityManager();
    }

    public synchronized List<SlapEvent> getEvents() {
        return entityManager.createNamedQuery("SlapEvent.findAllEvents", SlapEvent.class).getResultList();
    }

    public synchronized List<SlapEvent> getFutureEvents() {
        return entityManager.createNamedQuery("SlapEvent.findFutureEventsOrderedByStart", SlapEvent.class).getResultList();
    }

    /**
     * Gives back a event object
     *
     * @param ID event ID
     * @return Optional of Event object, empty when ID does not exist
     */
    public synchronized Optional<SlapEvent> getEventByID(String ID) throws IllegalArgumentException {
        try {
            SlapEvent event = entityManager.createNamedQuery("SlapEvent.findEventById", SlapEvent.class).setParameter("id", ID).getSingleResult();
            return Optional.of(event);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public synchronized Optional<SlapEvent> getNextEvent() {
        try {
            SlapEvent event = entityManager.createNamedQuery("SlapEvent.findFutureEventsOrderedByStart", SlapEvent.class)
                    .setMaxResults(1)
                    .getSingleResult();
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
            entityManager.getTransaction().begin();
            entityManager.persist(event);
            entityManager.getTransaction().commit();
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
            entityManager.getTransaction().begin();
            entityManager.remove(event);
            entityManager.getTransaction().commit();
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

    /**
     * Get currently running event or the upcoming one.
     *
     * @return current or next event.
     */
    public synchronized SlapEvent getCurrentOrNextEvent() {
        return null;
    }
}
