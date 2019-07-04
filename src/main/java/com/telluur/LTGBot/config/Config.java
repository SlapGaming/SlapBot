package com.telluur.LTGBot.config;

import lombok.Getter;

/**
 * Bean for the config.yaml file.
 *
 * @author Rick Fontein
 */

public class Config {

    /**
     * The discord login token
     */
    @Getter private String token;

    /**
     * The owner user id.
     */
    @Getter private String owner;

    /**
     * The admin group id.
     */
    @Getter private String admin;

    /**
     * The moderator group id.
     */
    @Getter private String moderator;

    /**
     * The prefix for commands
     */
    @Getter private String prefix;

    /**
     * The alternative prefix for commands
     */
    @Getter private String altprefix;

    /**
     * The text channel the bot should be locked to.
     */
    @Getter private String channel;

}
