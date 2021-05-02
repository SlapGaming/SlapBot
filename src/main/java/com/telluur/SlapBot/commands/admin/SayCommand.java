package com.telluur.SlapBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.AdminCommand;
import net.dv8tion.jda.api.entities.Message;

public class SayCommand extends AdminCommand {
    public SayCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "say";
        this.arguments = "<Message>";
        this.echoUserIssue = false;
        this.clearUserIssue = true;
        this.hidden = true;
        this.guildOnly = true;
    }

    @Override
    public void handle(CommandEvent event) {
        String message = event.getArgs();
        if (message != null && !message.equals("")) {
            Message referencedMessage = event.getMessage().getReferencedMessage();
            if (referencedMessage != null) {
                referencedMessage.reply(message).queue();
            } else {
                event.reply(message);
            }
        } else {
            event.replyInDm("Please include a `<message>` when using the `say` command.");
        }
    }
}
