package com.telluur.LTGBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.UserCommand;

/**
 * Simple ping-pong command
 *
 * @author Rick Fontein
 */

public class PingCmd extends UserCommand {
    public PingCmd(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "ping";
        this.help = "Ping pong me all day long!";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        event.replySuccess("Pong!");
        logger.info("Pong!");
    }
}
