package com.telluur.SlapBot.commands.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.ModeratorCommand;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

import static com.jagrosh.jdautilities.commons.utils.FinderUtil.findRoles;

/**
 * Delete a LTG role from discord and internally from storage.
 *
 * @author Rick Fontein
 */

public class RemoveGameCommand extends ModeratorCommand {
    public RemoveGameCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "removegame";
        this.arguments = "<@role>";
        this.help = "Delete a LTG role.";
        this.guildOnly = false;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void handle(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("Please include a single valid `<@role>` as argument.");
            return;
        }

        String[] parts = event.getArgs().split("\\s+");
        List<Role> mentionedRoles = findRoles(parts[0], slapBot.getGuild());
        if (mentionedRoles.size() != 1) {
            event.replyError("Please include a single valid `<@role>` as argument.");
            return;
        }

        Role LTGRole = mentionedRoles.get(0);

        slapBot.getLtgHandler().deleteGameRole(LTGRole,
                success -> {
                    String reply = String.format("Deleted LTG role %s.", LTGRole.getName());
                    event.replySuccess(reply);
                    String logNSA = String.format("LTG | **%s** deleted LTG role __%s__", event.getAuthor().getName(), LTGRole.getName());
                    slapBot.getNsaTxChannel().sendMessage(logNSA).queue();
                },
                failure -> event.replyError(failure.getMessage()));
    }
}
