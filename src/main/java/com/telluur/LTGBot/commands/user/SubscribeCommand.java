package com.telluur.LTGBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.UserCommand;

/**
 * Command that lets members join a game role
 *
 * @author Rick Fontein
 */

public class SubscribeCommand extends UserCommand {
    public SubscribeCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "subscribe";
        this.aliases = new String[]{"sub", "join"};
        this.arguments = "<@role>";
        this.help = "Subscribes to a game group";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {

        if (event.getArgs().isEmpty()) {
            event.replyError("Please include a role as argument");
            return;
        } else {
            System.out.println(event.getArgs());
        }

        //TODO implement
    }
}
