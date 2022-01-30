package com.telluur.SlapBot.features.ltg.listeners;

import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.features.ltg.jpa.LTGQuickSubscribe;
import com.telluur.SlapBot.features.ltg.jpa.LTGQuickSubscribeRepository;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Handles the quicks subscribe emojis underneath selected LTG messages.
 *
 * @author Rick Fontein
 */

public class QuickSubscribeListener extends ListenerAdapter {
    public static final String SUBSCRIBE_LABEL = "LTG";
    private final SlapBot bot;
    private final LTGQuickSubscribeRepository repository;

    public QuickSubscribeListener(SlapBot bot) {
        this.bot = bot;
        this.repository = new LTGQuickSubscribeRepository(bot.getEntityManagerFactory());
    }

    public void sendQuicksubMessage(TextChannel tx, Role role) {
        Message msg = new MessageBuilder(
                String.format("Hit the button below to join `%s`.", role.getName()))
                .setActionRows(ActionRow.of(
                        Button.primary(QuickSubscribeListener.SUBSCRIBE_LABEL, "Subscribe"),
                        Button.link("https://discord.com/channels/276858200853184522/596030791214170112/663754415504752650", "What's this?")
                )).build();
        tx.sendMessage(msg).queue(m -> {
            try {
                repository.save(new LTGQuickSubscribe(m.getId(), role.getId()));
            } catch (IOException e) {
                e.printStackTrace();
                //TODO notify of failed DB connection...
            }
        });
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().equals(SUBSCRIBE_LABEL)) {
            event.deferReply(true).queue();
            InteractionHook hook = event.getHook();

            Optional<LTGQuickSubscribe> qs = repository.findId(event.getMessageId());
            if (!qs.isPresent()) {
                hook.editOriginal("Could not find the LTG role for this button...").queue();
            } else {
                TextChannel textChannel = event.getTextChannel();
                Role role = bot.getGuild().getRoleById(qs.get().getRoleId());
                if (role == null) {
                    hook.editOriginal("Cannot subscribe to a game that does not exist (anymore).").queue();
                } else {
                    Member subscriber = bot.getGuild().getMember(event.getUser());
                    if (Objects.requireNonNull(subscriber).getRoles().contains(role)) {

                        hook.editOriginal(String.format("You are already subscribed to `%s`.", role.getName())).queue();
                    } else {
                        bot.getLtgHandler().joinGameRole(role, subscriber.getUser(),
                                success -> {
                                    String reply = String.format("You are now subscribed to `%s`.", role.getName());
                                    hook.editOriginal(reply).queue();
                                    String announcement = String.format("`%s` is now subscribed to `%s`.", subscriber.getEffectiveName(), role.getName());
                                    textChannel.sendMessage(announcement).queue();
                                    String logNSA = String.format("LTG | **%s** subscribed to __%s__", subscriber.getEffectiveName(), role.getName());
                                    bot.getNsaTxChannel().sendMessage(logNSA).queue();
                                },
                                failure -> hook.editOriginal("Uh-oh, something went wrong...\nPlease try manually joining this role with !join").queue());
                    }
                }
            }
        }
    }
}
