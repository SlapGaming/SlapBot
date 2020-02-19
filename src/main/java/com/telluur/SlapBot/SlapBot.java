package com.telluur.SlapBot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.telluur.SlapBot.features.ltg.LTGHandler;
import com.telluur.SlapBot.features.slapevents.SlapEventStorageHandler;
import com.telluur.SlapBot.system.config.Config;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.joda.time.DateTimeZone;

import java.awt.*;
import java.io.IOException;

/**
 * Singleton JDA bot class
 *
 * @author Rick Fontein
 */

public class SlapBot {
    /*
    In bot constants
     */
    public static final DateTimeZone TIME_ZONE = DateTimeZone.forID("Europe/London");
    public static final Color COLOR = Color.ORANGE;
    private final Config config;
    /*
    Handlers
     */
    @Getter private JDA jda;
    @Getter private LTGHandler ltgHandler;
    @Getter private SlapEventStorageHandler slapEventStorageHandler;
    @Getter private EventWaiter eventWaiter;
    /*
    Config stuff
     */
    @Getter private String prefix, altPrefix; //String

    public SlapBot(Config config, EventWaiter eventWaiter) {
        this.config = config;
        this.prefix = config.getPrefix();
        this.altPrefix = config.getAltprefix();
        this.eventWaiter = eventWaiter;
    }

    void finishBot(JDA jda) throws IOException {
        this.jda = jda;
        this.ltgHandler = new LTGHandler(this);
        this.slapEventStorageHandler = new SlapEventStorageHandler();
    }


    public Guild getGuild() {
        return jda.getGuildById(config.getGuild());
    }

    public User getOwner() {
        return jda.getUserById(config.getOwner());
    }

    public Role getAdminRole() {
        return jda.getRoleById(config.getAdmin());
    }

    public Role getModeratorRole() {
        return jda.getRoleById(config.getModerator());
    }

    public Role getPunRole() {
        return jda.getRoleById(config.getPunRole());
    }

    public TextChannel getGenTxChannel() {
        return jda.getTextChannelById(config.getGenTxChannel());
    }

    public TextChannel getLtgTxChannel() {
        return jda.getTextChannelById(config.getLtgTxChannel());
    }

    public TextChannel getNsaTxChannel() {
        return jda.getTextChannelById(config.getNsaTxChannel());
    }

    public VoiceChannel getPunVcChannel() {
        return jda.getVoiceChannelById(config.getPunVcChannel());
    }
}
