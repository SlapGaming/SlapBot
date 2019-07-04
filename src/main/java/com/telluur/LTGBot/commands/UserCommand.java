package com.telluur.LTGBot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commands accessible to users and higher
 *
 * @author Rick Fontein
 */

public abstract class UserCommand extends Command {

    protected static final Logger logger = LoggerFactory.getLogger("USER");

    public UserCommand() {
        this.category = new Category("User");
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
