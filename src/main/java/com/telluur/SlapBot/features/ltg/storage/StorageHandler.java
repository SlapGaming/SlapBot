package com.telluur.SlapBot.features.ltg.storage;

/**
 * Handles the yaml storage for the LTG games.
 *
 * @author Rick Fontein
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the YAML IO storage for LTG.
 *
 * @author Rick Fontein
 */

public class StorageHandler {
    private static final Logger logger = LoggerFactory.getLogger("LTG");
    private File storage;
    private ObjectMapper mapper;
    private ObjectNode root;

    /**
     * Initializes the handler by:
     * - opening the file
     * - reading the contents
     */
    public StorageHandler() throws IOException {
        storage = new File("yaml/storage.yaml");
        mapper = new ObjectMapper(new YAMLFactory());




        // Read the file and print all top level games
        root = (ObjectNode) mapper.readTree(storage);


        if (root == null) {
            // Storage file as empty, create root.
            root = mapper.createObjectNode();
            logger.info("No games in storage");
        } else {
            root.fields().forEachRemaining(
                    e -> logger.info(String.format("Loaded LTG: <%s> %s", e.getKey(), e.getValue().toString()))
            );
            //TODO check if roles weren't manually deleted from discord.
        }
    }

    /*
    GET/SET
    These methods are synchronized as the commands are executed async
     */

    /**
     * Checks whether the role exists in storage
     *
     * @param snowflake the discord group snowflake id
     * @return whether the game is in storage
     */
    public synchronized boolean hasGameBySnowflake(String snowflake) {
        return root.has(snowflake);
    }


    /**
     * Gives back a game object
     *
     * @param snowflake the discord group snowflake id
     * @return game object
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    public synchronized StoredGame getGameBySnowflake(String snowflake) throws IOException, IllegalArgumentException {
        if (root.has(snowflake)) {
            return mapper.treeToValue(root.get(snowflake), StoredGame.class);
        } else {
            throw new IllegalArgumentException("Game Snowflake does not exist.");
        }
    }

    /**
     * (Over)writes a game object to the internal state
     *
     * @param snowflake the discord group snowflake id
     * @param game      the game object
     * @throws IOException
     */
    public synchronized void setGameBySnowflake(String snowflake, StoredGame game) throws IOException {
        root.putPOJO(snowflake, game);
        mapper.writeValue(storage, root);
        writeToStorage();
    }

    /**
     * Deletes a game from storage
     *
     * @param snowflake the discord snowflake that identifies the game role
     * @throws IOException
     */
    public synchronized void deleteGameBySnowflake(String snowflake) throws IOException {
        if (root.has(snowflake)) {
            root.remove(snowflake);
            writeToStorage();
        } else {
            throw new IllegalArgumentException("Game Snowflake does not exist.");
        }
    }

    public synchronized List<String> getGameSnowflakes() {
        LinkedList<String> snowflakes = new LinkedList<>();
        root.fieldNames().forEachRemaining(snowflakes::add);
        return snowflakes;
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
     * TODO check if roles weren't manually deleted from discord.
     */
    public void forceReload() throws IOException {
        root = (ObjectNode) mapper.readTree(storage);
    }

}
