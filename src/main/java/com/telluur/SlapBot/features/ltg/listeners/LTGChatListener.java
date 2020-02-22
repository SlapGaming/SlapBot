package com.telluur.SlapBot.features.ltg.listeners;

import com.telluur.SlapBot.SlapBot;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Transforms LTG game role mentions into fancy messages.
 *
 * @author Rick Fontein
 */

public class LTGChatListener implements EventListener {
    private final SlapBot bot;


    public LTGChatListener(SlapBot bot) {
        this.bot = bot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        //Chat listener
        if (event instanceof MessageReceivedEvent) {
            Message message = ((MessageReceivedEvent) event).getMessage();

            //Skip messages from the bot
            if (message.getAuthor().isBot()) {
                return;
            }

            //Skip command, as we're not interested in commands.
            if (message.getContentRaw().startsWith(bot.getPrefix()) || message.getContentRaw().startsWith(bot.getAltPrefix())) {
                return;
            }

            //Only listen to messages in guild text channels
            if (!message.isFromType(ChannelType.TEXT)) {
                return;
            }

            //Filter out the WOW section
            if (Objects.requireNonNull(bot.getGuild().getCategoryById("663438826890330133")).getTextChannels().contains(message.getTextChannel())) {
                return;
            }

            //Message is not in the guild we're interested in.
            TextChannel textChannel = message.getTextChannel();
            List<TextChannel> guildChannels = bot.getGuild().getTextChannels();
            if (!guildChannels.contains(textChannel)) {
                return;
            }

            //Check whether the message contains a role mention and if that role is a LTG role
            List<Role> foundRoles = message.getMentionedRoles();
            if (foundRoles.size() == 1 && bot.getLtgHandler().isGameRole(foundRoles.get(0))) {
                //Delete the original message and create text embed
                Role role = foundRoles.get(0);

                String msg = String.format("**Looking-to-game notifier**\r\n" +
                                "> %s: %s\r\n" +
                                "Click on %s to quick subscribe to this game.",
                        Objects.requireNonNull(message.getMember()).getAsMention(), message.getContentRaw(), QuickSubscribeListener.SUBSCRIBE);
                textChannel.sendMessage(EmojiParser.parseToUnicode(msg)).queue(m -> {
                    bot.getQuickSubscribeListener().addButton(m, role);
                });

                message.delete().queue();
            }
        }
    }
}
