package com.telluur.SlapBot.features.slapevents.jpa;



import java.io.IOException;
import java.util.List;

public class SlapEventRepository {
    /**
     * Checks wether the event has the description, begin and end non null
     *
     * @param event Event to be checked
     * @return whether event is valid
     */
    public static boolean isValidEvent(SlapEvent event) {

    }

    /**
     * Checks whether the event ID exists in storage
     *
     * @param ID event ID
     * @return whether the event exists
     */
    public synchronized boolean hasEventByID(String ID) {

    }

    /**
     * Gives back a event object
     *
     * @param ID event ID
     * @return event object
     * @throws IllegalArgumentException
     */
    public synchronized SlapEvent getEventByID(String ID) throws IOException, IllegalArgumentException {
    }

    /**
     * (Over)writes an event object to the internal state
     *
     * @param id    the discord group snowflake id
     * @param event the game object
     * @throws IOException
     */
    public synchronized void setEventByID(String ID, SlapEvent event) throws IOException {
    }

    /**
     * Deletes an event from storage
     *
     * @param ID the id that identifies the event
     * @throws IOException
     */
    public synchronized void deleteEventByID(String ID) throws IOException {
    }

    /*
    Expanded Functionality
    These methods implement further user facing functionality
    These methods are synchronized as the commands are executed async
     */

    public synchronized List<String> getEventIDs() {
    }

    /**
     * Of all events, lists those that are
     * - have a start and end
     * - end date is in the future
     * - ordered by start state
     *
     * @return ordered events by start date
     */
    public synchronized List<SlapEvent> getValidFutureEventsOrderedByStart() {
    }

    /**
     * Get currently running event or the upcoming one.
     *
     * @return current or next event.
     */
    public synchronized SlapEvent getCurrentOrNextEvent() {
    }

    /**
     * Deletes the event where the start date is in the past.
     */
    public synchronized void pruneOldEvents() {
    }


}
