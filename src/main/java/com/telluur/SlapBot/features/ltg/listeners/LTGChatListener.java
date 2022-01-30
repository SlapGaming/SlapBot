package com.telluur.SlapBot.features.ltg.listeners;

import com.telluur.SlapBot.SlapBot;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Transforms LTG game role mentions into fancy messages.
 *
 * @author Rick Fontein
 */

public class LTGChatListener implements EventListener {
    private static final String LTGTXChannel = "596030791214170112"; //TODO Move to bot config
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

            //Limit it to the LTG Channel
            TextChannel textChannel = message.getTextChannel();
            if (!textChannel.getId().equals(LTGTXChannel)) {
                return;
            }

            //Check whether the message contains a role mention and if that role is a LTG role
            List<Role> foundRoles = message.getMentionedRoles();
            if (foundRoles.size() == 1 && bot.getLtgHandler().isGameRole(foundRoles.get(0))) {
                //Reply with a quick subscribe message
                Role role = foundRoles.get(0);
                bot.getQuickSubscribeListener().sendQuicksubMessage(textChannel, role);
            }
        }
    }
}
