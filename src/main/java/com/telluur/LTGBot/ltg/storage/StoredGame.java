package com.telluur.LTGBot.ltg.storage;

import lombok.Getter;
import lombok.Setter;

/**
 * POJO for the games stored in the storage.yaml file
 *
 * @author Rick Fontein
 */

public class StoredGame {
    @Getter @Setter private String name;

    /**
     * Constructor for adding a new game
     *
     * @param name the name of the game
     */
    public StoredGame(String name) {
        this.name = name;
    }


    /*
    INTERNAL
     */

    /**
     * Private constructor for Jackson
     * Uses Getters and Setters
     */
    private StoredGame() {
    }


    @Override
    public String toString() {
        return "StoredGame{" + "name='" + name + '\'' + '}';
    }
}
