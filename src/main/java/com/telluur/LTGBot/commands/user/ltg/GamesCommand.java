package com.telluur.LTGBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.UserCommand;

/**
 * Shows an alphabetical list of all LTG games including subscriber count
 *
 * @author Rick Fontein
 */

public class GamesCommand extends UserCommand {
    public GamesCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "games";
        this.help = "Shows an alphabetical list of all LTG games including subscriber count.";
    }

    @Override
    public void handle(CommandEvent event) {
        //TODO implement
        event.reply("Not implemented");
    }
}
