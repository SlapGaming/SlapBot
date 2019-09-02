package com.telluur.SlapBot.system.config;

import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;

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
     * The LTG text channel.
     */
    @Getter private String ltgTxChannel;

    /**
     * The NSA text channel.
     */
    @Getter private String nsaTxChannel;

    /**
     * The punishment voice channel
     */
    @Getter private String punVcChannel;

    /**
     * The punishment role
     */
    @Getter private String punRole;

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
    public Activity getGameStatus() {
        String parsedStatus = EmojiParser.parseToUnicode(status);
        switch (statustype) {
            case "watching":
                return Activity.watching(parsedStatus);
            case "listening":
                return Activity.listening(parsedStatus);
            case "playing":
                return Activity.playing(parsedStatus);
            default:
                return Activity.listening("for commands.");
        }
    }
}
