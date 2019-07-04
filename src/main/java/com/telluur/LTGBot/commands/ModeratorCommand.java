package com.telluur.LTGBot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.util.AccessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Commands accessible to moderators and higher
 *
 * @author Rick Fontein
 */

public abstract class ModeratorCommand extends Command {

    protected static final Logger logger = LoggerFactory.getLogger("MODERATOR");

    public ModeratorCommand(LTGBot ltgBot) {
        this.category = new Category("admin", commandEvent -> AccessUtil.isModerator(ltgBot, commandEvent.getMember()));
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
