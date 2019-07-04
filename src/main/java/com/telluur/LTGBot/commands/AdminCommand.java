package com.telluur.LTGBot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.util.AccessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commands accessible to admins and higher
 *
 * @author Rick Fontein
 */

public abstract class AdminCommand extends Command {

    protected static final Logger logger = LoggerFactory.getLogger("ADMIN");

    public AdminCommand(LTGBot ltgBot) {
        this.category = new Category("Admin", commandEvent -> AccessUtil.isAdmin(ltgBot, commandEvent.getMember()));
    }

    @Override
    protected void execute(CommandEvent event) {
        String author = event.getAuthor().getName();
        String cmd = event.getMessage().getContentDisplay();
        logger.info(String.format("%s: %s", author, cmd));

        handle(event);
    }

    public abstract void handle(CommandEvent event);
}
