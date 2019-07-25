package com.telluur.LTGBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.Main;
import com.telluur.LTGBot.commands.AdminCommand;
import com.vdurmont.emoji.EmojiParser;

/**
 * Command that kills the bot
 *
 * @author Rick Fontein
 */

public class KillCommand extends AdminCommand {
    public KillCommand(LTGBot ltgBot) {
        super(ltgBot);
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
