package com.telluur.LTGBot.commands.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.ModeratorCommand;

/**
 * TODO add class description
 *
 * @author Rick Fontein
 */

public class RemoveGameCommand extends ModeratorCommand {
    public RemoveGameCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "remove";
        this.aliases = new String[]{"delete", "del"};
        this.arguments = "<@role>";
        this.help = "Delete a game group";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        //TODO implement
    }
}
