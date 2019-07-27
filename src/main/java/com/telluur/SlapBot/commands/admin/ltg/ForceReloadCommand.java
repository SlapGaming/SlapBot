package com.telluur.SlapBot.commands.admin.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.AdminCommand;

import java.io.IOException;

/**
 * Forces a LTG storage load.
 *
 * @author Rick Fontein
 */

public class ForceReloadCommand extends AdminCommand {
    public ForceReloadCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "reload";
        this.help = "Forces to load LTG from storage. WARNING: ASYNC, MAY BREAK BOT.";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        try {
            slapBot.getLtgHandler().getStorageHandler().forceReload();
            event.replySuccess("Successfully reloaded from storage.");
        } catch (IOException e) {
            event.replyError("An IO Exception occurred. Good job, you broke the bot!");
        }

    }
}
