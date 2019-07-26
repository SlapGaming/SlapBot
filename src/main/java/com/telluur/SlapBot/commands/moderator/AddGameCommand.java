package com.telluur.LTGBot.commands.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.ModeratorCommand;

/**
 * Add an LTG game command
 *
 * @author Rick Fontein
 */

public class AddGameCommand extends ModeratorCommand {
    public AddGameCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "addgame";
        this.aliases = new String[]{"add", "create"};
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

        if (parts[0].contains("|")) {
            event.replyError("`<abbreviation=6>` contained the separation character `|`.");
            return;
        }

        if (parts[1].contains("|")) {
            event.replyError("`<fullname+>` contained the separation character `|`.");
            return;
        }

        if (parts[0].length() > 6) {
            event.replyError("`<abbreviation=6>` has a limit of 6 characters.");
            return;
        }

        ltgBot.getLtgHandler().createGameRole(parts[0], parts[1],
                role -> event.replySuccess(String.format("Created LTG role %s.", role.getAsMention())),
                failure -> event.replyError(failure.getMessage()));
    }
}
