package com.telluur.SlapBot.features.nsa;

import com.telluur.SlapBot.SlapBot;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NSAChatListener implements EventListener {
    private static final Logger logger = LoggerFactory.getLogger("NSA");

    private final SlapBot bot;

    private final Map<String, Message> db = new HashMap<>();

    public NSAChatListener(SlapBot bot) {
        this.bot = bot;
    }


    @Override
    public void onEvent(@NotNull GenericEvent event) {
        /*
        Log the messages in a in memory DB, so we can retrieve the content when deleted/updated.
         */
        if (event instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;

            //Do not log bot messages
            if (e.getAuthor().isBot()) {
                return;
            }

            //Don't log LTG messages
            if (e.getMessage().getMentionedRoles().stream().anyMatch(role -> bot.getLtgHandler().isGameRole(role))) {
                return;
            }

            //Don't log commands
            String raw = e.getMessage().getContentDisplay();
            if (raw.startsWith(bot.getPrefix()) || raw.startsWith(bot.getAltPrefix())) {
                return;
            }
            db.put(e.getMessageId(), e.getMessage());
        }




        /*
        Retrieve the deleted message, and echo to NSA channel
         */
        if (event instanceof GuildMessageDeleteEvent) {
            GuildMessageDeleteEvent e = (GuildMessageDeleteEvent) event;
            String msgID = e.getMessageId();
            if (db.containsKey(msgID)) {
                Message old = db.get(msgID);
                MessageBuilder echo = new MessageBuilder();

                //Header
                echo.append(String.format("DC | A message from **%s** in __%s__ was deleted:\n", old.getMember().getEffectiveName(), old.getTextChannel().getName()));

                //Old message contents:
                echo.append(String.format("> %s\n", old.getContentDisplay()));
                old.getAttachments().forEach(attachment -> echo.append(String.format("Attachment: %s\n", attachment.getProxyUrl())));

                //Build and split if needed.
                echo.buildAll(MessageBuilder.SplitPolicy.NEWLINE).forEach(echoPart -> bot.getNsaTxChannel().sendMessage(echoPart).queue());

            }
        }


        /*
        Retrieve the deleted message, and echo to NSA channel
         */
        if (event instanceof GuildMessageUpdateEvent) {
            GuildMessageUpdateEvent e = (GuildMessageUpdateEvent) event;
            String msgID = e.getMessageId();
            if (db.containsKey(msgID)) {
                Message old = db.get(msgID);
                Message updated = e.getMessage();

                MessageBuilder echo = new MessageBuilder();
                //Header
                echo.append(String.format("DC | A message from **%s** in __%s__ was updated.\n", old.getMember().getEffectiveName(), old.getTextChannel().getName()));

                //Old message contents
                echo.append(String.format("> %s\n", old.getContentDisplay()));
                old.getAttachments().forEach(a -> echo.append(String.format("Attachment: %s\n", a.getProxyUrl())));

                //New message contents
                echo.append(EmojiParser.parseToUnicode(":arrow_lower_right:\n"));
                echo.append(String.format("> %s\n", updated.getContentDisplay()));
                updated.getAttachments().forEach(a -> echo.append(String.format("Attachment: %s\n", a.getProxyUrl())));

                //Build and split if needed.
                echo.buildAll(MessageBuilder.SplitPolicy.NEWLINE).forEach(echoPart -> bot.getNsaTxChannel().sendMessage(echoPart).queue());

                //Update the db
                db.replace(msgID, updated);
            }
        }

    }
}
