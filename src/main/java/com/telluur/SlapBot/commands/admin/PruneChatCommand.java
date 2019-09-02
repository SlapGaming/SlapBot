package com.telluur.SlapBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.AdminCommand;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to clear a chat channel
 *
 * @author Rick Fontein
 */

public class PruneChatCommand extends AdminCommand {
    public PruneChatCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "prune";
        this.aliases = new String[]{"clear"};
        this.arguments = "<1-200>";
        this.help = "Deletes the last <1-200> messages in a textchannel, skips pinned messages.";
        this.guildOnly = true;
    }

    @Override
    public void handle(CommandEvent event) {
        //delete the message that triggered the clean
        event.getMessage().delete().queue();

        //parse arguments
        int limit = 0;

        if (event.getArgs().isEmpty()) {
            event.replyInDm("First argument should be a number. <1-100>");
        } else {
            String[] args = event.getArgs().split("\\s+");
            try {
                limit = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                event.replyInDm("First argument should be a number. <1-100>");
                return;
            }

            if (limit < 1 || limit > 200) {
                event.replyInDm("First argument should be a number. <1-100>");
                return;
            }
        }

        //Notify user in DM search has started.
        String reply = String.format("Trying to find messages in `%s`, limited to the last `%d` messages. :mag_right:", event.getChannel().getName(), limit);
        event.replyInDm(EmojiParser.parseToUnicode(reply));

        //Fetch last <?messages=25> and start delete process.
        event.getChannel().getHistoryBefore(event.getMessage(), limit).queue(
                (messageHistory -> {
                    List<Message> targetMessages = messageHistory.getRetrievedHistory().stream()
                            .filter(message -> !message.isPinned())
                            .collect(Collectors.toList());

                    if (targetMessages.size() > 0) {
                        event.replyInDm(EmojiParser.parseToUnicode(String.format("Queued `%d` for deletion :wastebasket:", targetMessages.size())));
                        targetMessages.forEach(message -> message.delete().queue());
                    } else {
                        event.replyInDm(EmojiParser.parseToUnicode("Could not find any commands/replies to delete. :shrug:"));
                    }
                })
        );
    }
}
