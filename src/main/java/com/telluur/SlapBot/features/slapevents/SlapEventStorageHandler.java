package com.telluur.SlapBot.features.slapevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SlapEventStorageHandler {
    private static final Logger logger = LoggerFactory.getLogger("EVENTS");
    private File storage;
    private ObjectMapper mapper;
    private ObjectNode root;

    /**
     * Initializes the handler by:
     * - opening the file
     * - reading the contents
     */
    public SlapEventStorageHandler() throws IOException {
        storage = new File("yaml/events.yaml");
        mapper = new ObjectMapper(new YAMLFactory());


        // Read the file
        try {
            root = (ObjectNode) mapper.readTree(storage);
        } catch (ClassCastException e) {
            root = null;
        }


        if (root == null) {
            // Storage file was empty, create root.
            root = mapper.createObjectNode();
            logger.info("No events found");
        } else {
            root.fields().forEachRemaining(
                    e -> logger.info(String.format("Loaded event: <%s> %s", e.getKey(), e.getValue().toString()))
            );
        }
    }

    /*
    GET/SET
    These methods are synchronized as the commands are executed async
     */

    /**
     * Checks whether the event ID exists in storage
     *
     * @param ID event ID
     * @return whether the event exists
     */
    public synchronized boolean hasEventByID(String ID) {
        return root.has(ID);
    }

    /**
     * Gives back a event object
     *
     * @param ID event ID
     * @return event object
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    public synchronized SlapEvent getEventByID(String ID) throws IOException, IllegalArgumentException {
        if (root.has(ID)) {
            return mapper.treeToValue(root.get(ID), SlapEvent.class);
        } else {
            throw new IllegalArgumentException("Event ID does not exist.");
        }
    }

    /**
     * (Over)writes an event object to the internal state
     *
     * @param id    the discord group snowflake id
     * @param event the game object
     * @throws IOException
     */
    public synchronized void setEventByID(String ID, SlapEvent event) throws IOException {
        root.putPOJO(ID, event);
        mapper.writeValue(storage, root);
        writeToStorage();
    }

    /**
     * Deletes an event from storage
     *
     * @param ID the id that identifies the event
     * @throws IOException
     */
    public synchronized void deleteEventByID(String ID) throws IOException {
        if (root.has(ID)) {
            root.remove(ID);
            writeToStorage();
        } else {
            throw new IllegalArgumentException("Event ID does not exist.");
        }
    }

    public synchronized List<String> getEventIDs() {
        LinkedList<String> IDs = new LinkedList<>();
        root.fieldNames().forEachRemaining(IDs::add);
        return IDs;
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
        List<SlapEvent> events = new ArrayList<>();
        root.fields().forEachRemaining(e -> {
            try {
                SlapEvent itEvent = mapper.treeToValue(e.getValue(), SlapEvent.class);
                events.add(itEvent);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        });
        DateTime now = new DateTime();

        return events.stream()
                .filter(e -> (isValidEvent(e) && now.isBefore(new DateTime(e.getEnd()))))
                .sorted(new SlapEventComparator())
                .collect(Collectors.toList());
    }

    /**
     * Checks wether the event has the description, begin and end non null
     *
     * @param event Event to be checked
     * @return wether event is valid
     */
    public static boolean isValidEvent(SlapEvent event) {
        return (event.getDescription() != null && event.getStart() != null && event.getEnd() != null);
    }

    /**
     * Get currently running event or the upcoming one.
     *
     * @return current or next event.
     */
    public synchronized SlapEvent getCurrentOrNextEvent() {
        List<SlapEvent> events = getValidFutureEventsOrderedByStart();
        DateTime now = new DateTime();

        for (SlapEvent event : events) {
            if (now.isBefore(new DateTime(event.getEnd()))) {
                return event;
            }
        }
        return null;
    }

    /**
     * Deletes the event where the start date is in the past.
     */
    public synchronized void pruneOldEvents() {
        //TODO Implement
    }


     /*
    SAVE FILE
     */

    /**
     * Writes the current internal state to the storage file,
     * WARNING: SYNCHRONIZED VERSION
     *
     * @throws IOException
     */
    private synchronized void writeToStorage() throws IOException {
        mapper.writeValue(storage, root);
    }


    /*
    FORCE WRITE/LOAD
    WARNING: These methods are NOT synchronized. May cause corrupt data/loss
     */

    /**
     * Force writes the current internal state to the storage file,
     * under normal operation, this only happens on bot shutdown.
     * WARNING: NOT SYNCHRONIZED
     *
     * @throws IOException
     */
    public void forceWrite() throws IOException {
        mapper.writeValue(storage, root);
    }

    /**
     * Reloads the file without saving the current state
     * WARNING: NOT SYNCHRONIZED
     */
    public void forceReload() throws IOException {
        root = (ObjectNode) mapper.readTree(storage);
    }
}
