package com.telluur.SlapBot.commands.abstractions;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commands accessible to users and higher
 *
 * @author Rick Fontein
 */

public abstract class UserCommand extends AbstractCommand {
    protected static final Logger logger = LoggerFactory.getLogger("USER");

    public UserCommand(SlapBot slapBot) {
        super(slapBot);
        this.category = new Category("User");
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (super.inValidGuildOrPrivate(event)) {
            String author = event.getAuthor().getName();
            String cmd = event.getMessage().getContentDisplay();
            logger.info(String.format("<OK> %s: %s", author, cmd));

            handle(event);
        }
    }

    public abstract void handle(CommandEvent event);
}
