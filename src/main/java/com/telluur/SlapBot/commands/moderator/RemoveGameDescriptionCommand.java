package com.telluur.SlapBot.commands.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.ModeratorCommand;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

import static com.jagrosh.jdautilities.commons.utils.FinderUtil.findRoles;

public class RemoveGameDescriptionCommand extends ModeratorCommand {
    public RemoveGameDescriptionCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "removegamedescription";
        this.aliases = new String[]{"rgd"};
        this.arguments = "<@role>";
        this.help = "Removes the LTG role description";
        this.guildOnly = false;
    }

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
        slapBot.getLtgHandler().deleteGameDescription(LTGRole,
                () -> {
                    String reply = String.format("Description for `%s` removed.", LTGRole.getName());
                    event.replySuccess(reply);
                    String logNSA = String.format("LTG | **%s** removed description for LTG role __%s__", event.getAuthor().getName(), LTGRole.getName());
                    slapBot.getNsaTxChannel().sendMessage(logNSA).queue();
                },
                fail -> event.replyError(String.format("Uh oh, something went wrong: %s", fail.getMessage()))
        );
    }
}
