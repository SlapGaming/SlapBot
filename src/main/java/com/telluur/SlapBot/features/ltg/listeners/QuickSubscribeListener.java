package com.telluur.SlapBot.features.ltg.listeners;

import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.features.ltg.jpa.LTGQuickSubscribe;
import com.telluur.SlapBot.features.ltg.jpa.LTGQuickSubscribeRepository;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * Handles the quicks subscribe emojis underneath selected LTG messages.
 *
 * @author Rick Fontein
 */

public class QuickSubscribeListener extends ListenerAdapter {
    public static final String SUBSCRIBE = EmojiParser.parseToUnicode(":video_game:");
    private final SlapBot bot;
    private final LTGQuickSubscribeRepository repository;

    public QuickSubscribeListener(SlapBot bot) {
        this.bot = bot;
        this.repository = new LTGQuickSubscribeRepository(bot.getEntityManagerFactory());
    }

    public void addButton(Message message, Role role) {
        try {
            repository.save(new LTGQuickSubscribe(message.getId(), role.getId()));
            message.addReaction(SUBSCRIBE).queue();
        } catch (IOException e) {
            message.editMessage(message.toString().concat("\n*Failed to register subscribe button...*")).queue();
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        //Check if this is a message we are monitoring. No guild checks etc required. Filter bots
        Optional<LTGQuickSubscribe> qs = repository.findId(event.getMessageId());
        if (!event.getUser().isBot() && qs.isPresent()) {
            //Subscribe a member if they aren't already.
            if (event.getReactionEmote().getName().equals(SUBSCRIBE)) {
                TextChannel textChannel = event.getTextChannel();
                Role role = bot.getGuild().getRoleById(qs.get().getRoleId());
                if (role == null) {
                    textChannel.sendMessage("Cannot subscribe to game that does not exist (anymore).").queue();
                } else {
                    Member subscriber = bot.getGuild().getMember(event.getUser());
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
