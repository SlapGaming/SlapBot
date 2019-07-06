package com.telluur.LTGBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.UserCommand;

/**
 * Command that lets members leave a game role
 *
 * @author Rick Fontein
 */

public class UnsubscribeCommand extends UserCommand {
    public UnsubscribeCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "unsubscribe";
        this.aliases = new String[]{"unsub", "leave"};
        this.arguments = "<@role>";
        this.help = "Unsubscribe from a game group";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        //TODO implement

    }
}
