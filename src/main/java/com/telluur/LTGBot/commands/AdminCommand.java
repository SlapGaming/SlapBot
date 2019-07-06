package com.telluur.LTGBot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.util.AccessUtil;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commands accessible to admins and higher
 *
 * @author Rick Fontein
 */

public abstract class AdminCommand extends AbstractCommand {
    protected static final Logger logger = LoggerFactory.getLogger("ADMIN");

    public AdminCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.category = new Category("Admin");
    }

    @Override
    protected void execute(CommandEvent event) {
        if (super.inValidGuildOrPrivate(event)) {
            User user = event.getAuthor();
            String cmd = event.getMessage().getContentDisplay();
            if (AccessUtil.isAdmin(ltgBot, user)) {
                logger.info(String.format("<OK> %s: %s", user.getName(), cmd));
                handle(event);
            } else {
                logger.info(String.format("<DENIED> %s: %s", user.getName(), cmd));
            }
        }
    }

    public abstract void handle(CommandEvent event);
}
