package com.telluur.SlapBot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;

public class VersionCommand extends UserCommand {
    public VersionCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "version";
        this.help = "Displays the bots version.";
        this.guildOnly = false;

        this.echoUserIssue = false;
    }

    @Override
    public void handle(CommandEvent event) {
        event.reply(String.format("SlapBot version: `%s`", SlapBot.VERSION));
    }
}
