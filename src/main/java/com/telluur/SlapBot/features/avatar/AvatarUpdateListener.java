package com.telluur.SlapBot.features.avatar;

import com.telluur.SlapBot.SlapBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;

/**
 * Sends a message to the main
 *
 * @author Rick Fontein
 */

public class AvatarUpdateListener implements EventListener {
    private final SlapBot bot;

    public AvatarUpdateListener(SlapBot bot) {
        this.bot = bot;
    }

    @Override
    public void onEvent(@Nonnull GenericEvent genericEvent) {
        if (genericEvent instanceof UserUpdateAvatarEvent) {
            UserUpdateAvatarEvent event = (UserUpdateAvatarEvent) genericEvent;

            Member member = bot.getGuild().getMember(event.getUser());
            if (member == null) {
                return;
            }

            String title = String.format("`%s` has a new avatar!", member.getEffectiveName());
            MessageEmbed message = AvatarMessageBuilder.buildEmbed(title, event.getNewAvatarUrl());
            bot.getGenTxChannel().sendMessage(message).queue();
        }
    }
}
