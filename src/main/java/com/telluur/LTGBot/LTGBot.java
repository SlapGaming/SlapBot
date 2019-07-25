package com.telluur.LTGBot;

import com.telluur.LTGBot.config.Config;
import com.telluur.LTGBot.ltg.LTGHandler;
import com.telluur.LTGBot.ltg.storage.StorageHandler;
import lombok.Getter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
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
    private final Config config;
    /*
    Handlers
     */
    @Getter LTGHandler ltgHandler;
    @Getter private JDA jda;
    /*
    Config stuff
     */
    @Getter private Guild guild;
    @Getter private User owner;
    @Getter private Role adminRole, moderatorRole;
    @Getter private TextChannel textChannel;
    @Getter private String prefix, altPrefix;

    public LTGBot(Config config) {
        this.config = config;
        this.prefix = config.getPrefix();
        this.altPrefix = config.getAltprefix();
    }

    public void finishBot(JDA jda) throws IllegalArgumentException {
        this.jda = jda;
        this.guild = jda.getGuildById(config.getGuild());
        this.owner = jda.getUserById(config.getOwner());
        this.adminRole = jda.getRoleById(config.getAdmin());
        this.moderatorRole = jda.getRoleById(config.getModerator());
        this.textChannel = jda.getTextChannelById(config.getChannel());

        this.ltgHandler = new LTGHandler(this);
    }
}
