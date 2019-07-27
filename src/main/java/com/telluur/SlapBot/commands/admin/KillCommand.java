package com.telluur.SlapBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.Main;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.AdminCommand;
import com.vdurmont.emoji.EmojiParser;

/**
 * Command that kills the bot
 *
 * @author Rick Fontein
 */

public class KillCommand extends AdminCommand {
    public KillCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "kill";
        this.aliases = new String[]{"fuckoff", "die"};
        this.help = "Kills the bot.";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        event.replySuccess(EmojiParser.parseToUnicode("Committing suicide `ε/̵͇̿̿/’̿’̿ ̿(◡︵◡)`"));
        Main.shutdown("Killed by user");
    }
}
