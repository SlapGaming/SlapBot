package com.telluur.SlapBot.commands.admin.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.AdminCommand;

import java.io.IOException;

/**
 * Forces a LTG storage write
 *
 * @author Rick Fontein
 */

public class ForceSaveCommand extends AdminCommand {
    public ForceSaveCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "save";
        this.help = "Forces to save LTG to storage. WARNING: ASYNC, MAY BREAK BOT.";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        try {
            slapBot.getLtgHandler().getStorageHandler().forceWrite();
            event.replySuccess("Successfully saved to storage.");
        } catch (IOException e) {
            event.replyError("An IO Exception occurred. Good job, you broke the bot!");
        }
    }
}
