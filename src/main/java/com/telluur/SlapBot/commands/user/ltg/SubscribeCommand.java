package com.telluur.LTGBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.UserCommand;
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
    public SubscribeCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "subscribe";
        this.aliases = new String[]{"sub", "join"};
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
        ltgBot.getLtgHandler().joinGameRole(LTGRole, subscriber,
                success -> event.replySuccess(String.format("%s is now subscribed to `%s`.", subscriber.getAsMention(), LTGRole.getName())),
                failure -> event.replyError(failure.getMessage()));
    }
}
