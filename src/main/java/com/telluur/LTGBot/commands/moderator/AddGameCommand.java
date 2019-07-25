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
        this.arguments = "<name>";
        this.help = "Adds a new LTG role.";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("Please include a `name` as argument.");
            return;
        }

        ltgBot.getLtgHandler().createGameRole(event.getArgs(),
                role -> event.replySuccess(String.format("Created LTG role %s.", role.getAsMention())),
                failure -> event.replyError(failure.getMessage()));
    }
}
