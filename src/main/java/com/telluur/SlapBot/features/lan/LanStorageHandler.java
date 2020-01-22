package com.telluur.SlapBot.features.lan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Handles the YAML IO for lan events.
 *
 * @author Rick Fontein
 */

public class LanStorageHandler {
    private static final Logger logger = LoggerFactory.getLogger("LAN");
    private static final String EVENT_KEY = "event";
    private static final String DATE_KEY = "date";

    private File storage;
    private ObjectMapper mapper;
    private ObjectNode root;

    /**
     * Initializes the handler by:
     * - opening the file
     * - reading the contents
     */
    public LanStorageHandler() throws IOException {
        storage = new File("yaml/lan.yaml");
        mapper = new ObjectMapper(new YAMLFactory());


        // Read the file and print all top level games
        root = (ObjectNode) mapper.readTree(storage);


        if (root == null) {
            // Storage file as empty, create root.
            root = mapper.createObjectNode();
            logger.info("No LAN event stored");
        } else if (root.hasNonNull("eventName") && root.hasNonNull("date")) {
            logger.info(String.format("Loaded LAN event \"%s\" on %s", root.get("eventName"), root.get("date")));
        }
    }

    public synchronized String getEventName() throws IOException {
        if (root.hasNonNull(EVENT_KEY)) {
            return root.get(EVENT_KEY).asText();
        } else {
            throw new IOException("Could not get event.");
        }
    }

    public synchronized void setEventName(String eventName) throws IOException {
        root.put(EVENT_KEY, eventName);
        writeToStorage();
    }

    public synchronized DateTime getDate() throws IOException {
        if (root.hasNonNull(DATE_KEY)) {
            String iso = root.get(DATE_KEY).asText();
            return new DateTime(iso);
        } else {
            throw new IOException("Could not get date.");
        }
    }

    public synchronized void setDate(DateTime eventDate) throws IOException {
        root.put(DATE_KEY, eventDate.toDateTimeISO().toString());
        writeToStorage();
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
