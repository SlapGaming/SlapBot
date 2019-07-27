package com.telluur.SlapBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

import static com.jagrosh.jdautilities.commons.utils.FinderUtil.findRoles;

/**
 * Command that lets members join a game role
 *
 * @author Rick Fontein
 */

public class SubscribeCommand extends UserCommand {
    public SubscribeCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "join";
        this.aliases = new String[]{"subscribe", "sub"};
        this.arguments = "<@role>";
        this.help = "Subscribes to a game group.";
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void handle(CommandEvent event) {
        //Delete the message as it includes a role mention
        event.getMessage().delete().queue();

        if (event.getArgs().isEmpty()) {
            event.replyError("Please include a `<@role>` as argument.");
            return;
        }

        String[] parts = event.getArgs().split("\\s+");
        List<Role> mentionedRoles = findRoles(parts[0], event.getGuild());
        if (mentionedRoles.size() != 1) {
            event.replyError("Please include a single valid `<@role>` as argument.");
            return;
        }

        Role LTGRole = mentionedRoles.get(0);
        User subscriber = event.getAuthor();
        slapBot.getLtgHandler().joinGameRole(LTGRole, subscriber,
                success -> event.replySuccess(String.format("%s is now subscribed to `%s`.", subscriber.getAsMention(), LTGRole.getName())),
                failure -> event.replyError(failure.getMessage()));
    }
}
