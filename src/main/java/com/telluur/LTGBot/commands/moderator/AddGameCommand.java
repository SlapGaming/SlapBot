package com.telluur.LTGBot.commands.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.ModeratorCommand;

/**
 * TODO add class description
 *
 * @author Rick Fontein
 */

public class AddGameCommand extends ModeratorCommand {
    public AddGameCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "add";
        this.aliases = new String[]{"new", "create"};
        this.arguments = "<identifier>";
        this.help = "Adds a new game role";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        //TODO Implement
    }
}
