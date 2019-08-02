package com.telluur.SlapBot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.telluur.SlapBot.config.Config;
import com.telluur.SlapBot.ltg.LTGHandler;
import lombok.Getter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Singleton JDA bot class
 *
 * @author Rick Fontein
 */

public class SlapBot {
    static final Logger logger = LoggerFactory.getLogger("BOT");
    @Getter private static final Color COLOR = new Color(255, 90, 50);
    private final Config config;
    /*
    Handlers
     */
    @Getter private JDA jda;
    @Getter private LTGHandler ltgHandler;
    @Getter private EventWaiter eventWaiter;
    /*
    Config stuff
     */
    @Getter private Guild guild;
    @Getter private User owner;
    @Getter private Role adminRole, moderatorRole, punRole;
    @Getter private TextChannel ltgTxChannel;
    @Getter private VoiceChannel punVcChannel;
    @Getter private String prefix, altPrefix;

    public SlapBot(Config config, EventWaiter eventWaiter) {
        this.config = config;
        this.prefix = config.getPrefix();
        this.altPrefix = config.getAltprefix();
        this.eventWaiter = eventWaiter;
    }

    void finishBot(JDA jda) throws IllegalArgumentException {
        this.jda = jda;
        this.guild = jda.getGuildById(config.getGuild());
        this.owner = jda.getUserById(config.getOwner());
        this.adminRole = jda.getRoleById(config.getAdmin());
        this.moderatorRole = jda.getRoleById(config.getModerator());
        this.ltgTxChannel = jda.getTextChannelById(config.getLtgTxChannel());
        this.punRole = jda.getRoleById(config.getPunRole());
        this.punVcChannel = jda.getVoiceChannelById(config.getPunVcChannel());

        this.ltgHandler = new LTGHandler(this);
    }
}
