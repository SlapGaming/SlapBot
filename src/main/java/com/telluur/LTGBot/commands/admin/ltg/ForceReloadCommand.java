package com.telluur.LTGBot.commands.admin.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.AdminCommand;

import java.io.IOException;

/**
 * Forces a LTG storage load.
 *
 * @author Rick Fontein
 */

public class ForceReloadCommand extends AdminCommand {
    public ForceReloadCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "reload";
        this.help = "Forces to load LTG from storage. WARNING: ASYNC, MAY BREAK BOT.";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        try {
            ltgBot.getLtgHandler().getStorageHandler().forceReload();
            event.replySuccess("Successfully reloaded from storage.");
        } catch (IOException e) {
            event.replyError("An IO Exception occurred. Good job, you broke the bot!");
        }

    }
}
