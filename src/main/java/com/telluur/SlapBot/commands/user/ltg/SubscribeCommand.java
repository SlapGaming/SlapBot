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
        this.guildOnly = false;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void handle(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("Please include a `<@role>` as argument.");
            return;
        }

        String[] parts = event.getArgs().split("\\s+");
        List<Role> mentionedRoles = findRoles(parts[0], slapBot.getGuild());
        if (mentionedRoles.size() != 1) {
            event.replyError("Please include a single valid `<@role>` as argument.");
            return;
        }

        Role LTGRole = mentionedRoles.get(0);
        User subscriber = event.getAuthor();
        slapBot.getLtgHandler().joinGameRole(LTGRole, subscriber,
                success -> {
                    String reply = String.format("`%s` is now subscribed to `%s`.", subscriber.getName(), LTGRole.getName());
                    event.replySuccess(reply);
                    String logNSA = String.format("LTG | **%s** subscribed to __%s__", subscriber.getName(), LTGRole.getName());
                    slapBot.getNsaTxChannel().sendMessage(logNSA).queue();
                },
                failure -> event.replyError(failure.getMessage()));
    }
}
