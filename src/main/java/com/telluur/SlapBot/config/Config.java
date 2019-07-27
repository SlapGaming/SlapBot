package com.telluur.SlapBot.config;

import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Game;

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
     * The guild ID the bot should be locked to
     */
    @Getter private String guild;

    /**
     * The text channel the bot should be locked to.
     */
    @Getter private String channel;

    /**
     * The bot's owner user id.
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
     * The status type of the bot, either playing, watching or listening
     */
    @Getter private String statustype;

    /**
     * The status of the bot
     */
    @Getter private String status;

    /**
     * Fully prepares a JDA Game object.
     *
     * @return Game object containing the statustype and status
     */
    public Game getGameStatus() {
        String parsedStatus = EmojiParser.parseToUnicode(status);
        switch (statustype) {
            case "watching":
                return Game.watching(parsedStatus);
            case "listening":
                return Game.listening(parsedStatus);
            case "playing":
                return Game.playing(parsedStatus);
            default:
                return Game.listening("for commands.");
        }
    }
}
