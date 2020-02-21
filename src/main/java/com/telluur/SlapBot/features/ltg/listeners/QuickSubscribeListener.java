package com.telluur.SlapBot.features.ltg.listeners;

import com.telluur.SlapBot.SlapBot;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

/**
 * Handles the quicks subscribe emojis underneath selected LTG messages.
 *
 * @author Rick Fontein
 */

public class QuickSubscribeListener implements EventListener {
    public static final String SUBSCRIBE = EmojiParser.parseToUnicode(":video_game:");
    private final SlapBot bot;
    private HashMap<String, Role> buttons = new HashMap<>();

    public QuickSubscribeListener(SlapBot bot) {
        this.bot = bot;
    }

    public void addButton(Message message, Role role) {
        message.addReaction(SUBSCRIBE).queue();
        buttons.put(message.getId(), role);
        System.out.println("button added.");
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof MessageReactionAddEvent) {
            MessageReactionAddEvent mrea = (MessageReactionAddEvent) event;
            //Check if this is a message we are monitoring. No guild checks etc required. Filter bots
            if (!mrea.getUser().isBot() && buttons.containsKey(mrea.getMessageId())) {
                //Subscribe a member if they aren't already.
                if (mrea.getReactionEmote().getName().equals(SUBSCRIBE)) {
                    Role role = buttons.get(mrea.getMessageId());
                    Member subscriber = bot.getGuild().getMember(mrea.getUser());
                    TextChannel textChannel = mrea.getTextChannel();

                    if (Objects.requireNonNull(subscriber).getRoles().contains(role)) {
                        subscriber.getUser().openPrivateChannel().queue(
                                priv -> priv.sendMessage(String.format("You are already subscribed to `%s`.", role.getName())).queue()
                        );
                    } else {
                        bot.getLtgHandler().joinGameRole(role, subscriber.getUser(),
                                success -> {
                                    String reply = String.format("`%s` is now subscribed to `%s`.", subscriber.getEffectiveName(), role.getName());
                                    textChannel.sendMessage(reply).queue();
                                    String logNSA = String.format("LTG | **%s** subscribed to __%s__", subscriber.getEffectiveName(), role.getName());
                                    bot.getNsaTxChannel().sendMessage(logNSA).queue();
                                },
                                failure -> textChannel.sendMessage(failure.getMessage()).queue());
                    }
                }
            }
        }
    }
}
