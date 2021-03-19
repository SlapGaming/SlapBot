package com.telluur.SlapBot.commands.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.ModeratorCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

import static com.jagrosh.jdautilities.commons.utils.FinderUtil.findRoles;

public class SetGameDescriptionCommand extends ModeratorCommand {
    public SetGameDescriptionCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "setgamedescription";
        this.aliases = new String[]{"sgd"};
        this.arguments = "<@role>";
        this.help = "Sets the replied to message as the game description.";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        Message msg = event.getMessage().getReferencedMessage();
        if (msg == null) {
            event.replyError(String.format("Create the new description message first by typing it in discord. Then _reply_ to that message with `%s%s <@role>`.", slapBot.getPrefix(), this.name));
            return;
        }

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
        slapBot.getLtgHandler().addGameDescription(LTGRole, msg,
                () -> {
                    String reply = String.format("Description for `%s` updated.", LTGRole.getName());
                    event.replySuccess(reply);
                    String logNSA = String.format("LTG | **%s** updated description for LTG role __%s__", event.getAuthor().getName(), LTGRole.getName());
                    slapBot.getNsaTxChannel().sendMessage(logNSA).queue();
                },
                fail -> event.replyError(String.format("Uh oh, something went wrong: %s", fail.getMessage()))
        );
    }
}
