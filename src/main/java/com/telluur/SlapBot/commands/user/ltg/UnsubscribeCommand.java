package com.telluur.SlapBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

import static com.jagrosh.jdautilities.commons.utils.FinderUtil.findRoles;

/**
 * Command that lets members leave a game role
 *
 * @author Rick Fontein
 */

public class UnsubscribeCommand extends UserCommand {
    public UnsubscribeCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "leave";
        this.aliases = new String[]{"unsubscribe", "unsub"};
        this.arguments = "<@role>";
        this.help = "Unsubscribe from a game group.";
        this.guildOnly = false;
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
        slapBot.getLtgHandler().leaveGameRole(LTGRole, subscriber,
                success -> event.replySuccess(String.format("%s unsubscribed from `%s`.", subscriber.getAsMention(), LTGRole.getName())),
                failure -> event.replyError(failure.getMessage()));
    }
}
