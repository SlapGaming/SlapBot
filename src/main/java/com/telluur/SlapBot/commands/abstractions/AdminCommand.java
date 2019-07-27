package com.telluur.SlapBot.commands.abstractions;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.util.AccessUtil;
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

    public AdminCommand(SlapBot slapBot) {
        super(slapBot);
        this.category = new Category("Admin");
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (super.inValidGuildOrPrivate(event)) {
            User user = event.getAuthor();
            String cmd = event.getMessage().getContentDisplay();
            if (AccessUtil.isAdmin(slapBot, user)) {
                logger.info(String.format("<OK> %s: %s", user.getName(), cmd));
                handle(event);
            } else {
                logger.info(String.format("<DENIED> %s: %s", user.getName(), cmd));
                event.replyError(String.format(
                        "This command can only be used by `%s`.",
                        slapBot.getAdminRole().getName()
                ));
            }
        }
    }

    public abstract void handle(CommandEvent event);
}
