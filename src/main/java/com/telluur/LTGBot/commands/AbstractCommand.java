package com.telluur.LTGBot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract command as super class for all commands
 * Filters commands to the configured guild
 *
 * @author Rick Fontein
 */

public abstract class AbstractCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger("CMD");
    protected LTGBot ltgBot;

    AbstractCommand(LTGBot ltgBot) {
        this.ltgBot = ltgBot;
    }

    boolean inValidGuildOrPrivate(CommandEvent event) {
        User user = event.getAuthor();
        String cmd = event.getMessage().getContentDisplay();
        Guild eventGuild = event.getGuild();
        Guild configGuild = ltgBot.getGuild();

        if (eventGuild != null && !eventGuild.equals(configGuild)) {
            logger.info(String.format("<DENIED> %s: %s", user.getName(), cmd));
            event.replyError(String.format(
                    "This bot is locked to `%s`, and can only be used there or in private chat.",
                    configGuild.getName()
            ));
            return false;
        } else {
            return true;
        }
    }
}
