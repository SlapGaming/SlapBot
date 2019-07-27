package com.telluur.SlapBot.commands.abstractions;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.util.AccessUtil;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Commands accessible to moderators and higher
 *
 * @author Rick Fontein
 */

public abstract class ModeratorCommand extends AbstractCommand {
    protected static final Logger logger = LoggerFactory.getLogger("MODERATOR");

    public ModeratorCommand(SlapBot slapBot) {
        super(slapBot);
        this.category = new Category("Moderator");
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (super.inValidGuildOrPrivate(event)) {
            User user = event.getAuthor();
            String cmd = event.getMessage().getContentDisplay();

            if (AccessUtil.isModerator(slapBot, user)) {
                logger.info(String.format("<OK> %s: %s", user.getName(), cmd));
                handle(event);
            } else {
                logger.info(String.format("<DENIED> %s: %s", user.getName(), cmd));
                event.replyError(String.format(
                        "This command can only be used by `%s` or `%s`.",
                        slapBot.getAdminRole().getName(),
                        slapBot.getModeratorRole().getName()
                ));
            }
        }
    }

    public abstract void handle(CommandEvent event);
}
