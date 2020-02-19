package com.telluur.SlapBot.commands.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.ModeratorCommand;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Add an LTG game command
 *
 * @author Rick Fontein
 */

public class AddGameCommand extends ModeratorCommand {
    private final List<Character> ILLEGAL_CHARS = "|\"'`(){}[]<>/"
            .chars()
            .mapToObj(i -> (char) i)
            .collect(Collectors.toList());

    public AddGameCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "addgame";
        this.arguments = "<abbreviation=6> <fullname+>";
        this.help = "Adds a new LTG role.";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        String[] parts = event.getArgs().split("\\s+", 2);
        if (parts.length < 2) {
            event.replyError("Please include a `<abbreviation=6>` and <fullname+> as argument.");
            return;
        }

        if (ILLEGAL_CHARS.stream().anyMatch(c -> event.getArgs().contains(c.toString()))) {
            event.replyError(String.format(
                    "Either the abbreviation or the fullname contained one of the following illegal characters: `%s`",
                    ILLEGAL_CHARS));
            return;
        }

        if (parts[0].length() > 6) {
            event.replyError("`<abbreviation=6>` has a limit of 6 characters.");
            return;
        }

        slapBot.getLtgHandler().createGameRole(parts[0], parts[1],
                role -> {
                    String reply = String.format("Created LTG role %s.", role.getAsMention());
                    event.replySuccess(reply);
                    String logNSA = String.format("LTG | **%s** created LTG role __%s__", event.getAuthor().getName(), role.getName());
                    slapBot.getNsaTxChannel().sendMessage(logNSA).queue();
                },
                failure -> event.replyError(failure.getMessage()));
    }
}
