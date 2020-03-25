package com.telluur.SlapBot.features.joinnotifier;

import com.telluur.SlapBot.SlapBot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;

/**
 * Adds a role to a new member when a specific join link has been used.
 *
 * @author Rick Fontein
 */

public class JoinNotifierListener implements EventListener {
    private final SlapBot bot;

    public JoinNotifierListener(SlapBot bot) {
        this.bot = bot;
    }

    @Override
    public void onEvent(@Nonnull GenericEvent genericEvent) {
        if (genericEvent instanceof GuildMemberJoinEvent) {
            GuildMemberJoinEvent event = (GuildMemberJoinEvent) genericEvent;
            Member member = event.getMember();

            bot.getGenTxChannel().sendMessage(
                    String.format(JoinMessages.randomJoinMessage(), member.getAsMention())
            ).queue();
        }
    }
}
