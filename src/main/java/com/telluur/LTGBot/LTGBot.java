package com.telluur.LTGBot;

import com.telluur.LTGBot.config.Config;
import lombok.Getter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Singleton JDA bot class
 *
 * @author Rick Fontein
 */

public class LTGBot {
    static final Logger logger = LoggerFactory.getLogger("BOT");

    @Getter private JDA jda;
    @Getter private User owner;
    @Getter private Role adminRole, moderatorRole;
    @Getter private TextChannel textChannel;
    @Getter private String prefix, altPrefix;
    private final Config config;


    public LTGBot(Config config) {
        this.config = config;

        this.prefix = config.getPrefix();
        this.altPrefix = config.getAltprefix();
    }

    public void finishBot(JDA jda) throws IllegalArgumentException {
        this.owner = jda.getUserById(config.getOwner());
        this.adminRole = jda.getRoleById(config.getAdmin());
        this.moderatorRole = jda.getRoleById(config.getModerator());
        this.textChannel = jda.getTextChannelById(config.getChannel());
    }
}
