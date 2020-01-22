package com.telluur.SlapBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

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
        this.guildOnly = true;
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
        Member member = event.getMember();
        User subscriber = member.getUser();
        slapBot.getLtgHandler().leaveGameRole(LTGRole, subscriber,
                success -> {
                    String reply = String.format("`%s` unsubscribed from `%s`.", member.getEffectiveName(), LTGRole.getName());
                    event.replySuccess(reply);
                    String logNSA = String.format("LTG | **%s** unsubscribed from __%s__", member.getEffectiveName(), LTGRole.getName());
                    slapBot.getNsaTxChannel().sendMessage(logNSA).queue();
                },
                failure -> event.replyError(failure.getMessage()));
    }
}
