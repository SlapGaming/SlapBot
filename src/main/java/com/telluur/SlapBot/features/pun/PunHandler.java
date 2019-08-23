package com.telluur.SlapBot.features.pun;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.util.AccessUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handles the punishment process
 *
 * @author Rick Fontein
 */

public class PunHandler {
    private SlapBot bot;
    private Map<Guild, List<Member>> punishedGuilds = new HashMap<>();

    public PunHandler(SlapBot slapBot) {
        this.bot = slapBot;
        forceUnpunish();
    }

    private void forceUnpunish() {
        //TODO Implement a clear role of all channels on bot startup.
    }

    public void canPunish(CommandEvent event, Punishment punishment) throws PunException {
        getPunishedIfValidPunishment(event, punishment);
    }

    private List<Member> getPunishedIfValidPunishment(CommandEvent event, Punishment punishment) throws PunException {
        Guild guild = event.getGuild();
        Member punMember = punishment.getPunMember();
        List<Member> punished;
        if (punishedGuilds.containsKey(guild)) {
            punished = punishedGuilds.get(guild);

            //check if member is already punished
            if (punished.contains(punMember)) {
                throw new PunException(String.format("%s is already punished.", punMember.getAsMention()));
            }

            //check if caller is being punished
            if (punished.contains(event.getMember())) {
                throw new PunException(String.format("%s is a bit salty and tried to punish while being punished. BAD BOI.", event.getMember().getAsMention()));
            }
        } else {
            punished = new ArrayList<>();
            punishedGuilds.put(guild, punished);
        }
        return punished;
    }


    public void punish(CommandEvent event, Punishment punishment) throws PunException {
        Member punMember = punishment.getPunMember();
        int timeout = punishment.getTimeout();

        //Fetch guild settings
        Guild guild = bot.getGuild();
        Role punRole = bot.getPunRole();
        VoiceChannel punVC = bot.getPunVcChannel();

        //fetch punished members of guild, create if doesn't exist yet
        List<Member> punished = getPunishedIfValidPunishment(event, punishment);

        GuildController gc = guild.getController();
        VoiceChannel origin = punMember.getVoiceState().getChannel();
        final boolean punRolable = !AccessUtil.hasHigherRoleThanBot(punMember);

        //Add role and move user if still in voice.
        punished.add(punMember);
        if (punRolable) {
            gc.addSingleRoleToMember(punMember, punRole).queue();
        }
        if (punMember.getVoiceState().inVoiceChannel()) {
            gc.moveVoiceMember(punMember, punVC).queue();
        }

        //Start Async task for moving the user back/removing pun role
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable unpunish = () -> {
            if (punRolable) {
                gc.removeSingleRoleFromMember(punMember, punRole).queue();
            }

            //check if user has left voice, or has already been moved to origin.
            if (punMember.getVoiceState().inVoiceChannel() && !punMember.getVoiceState().getChannel().equals(origin)) {
                gc.moveVoiceMember(punMember, origin).queue();
            }
            punished.remove(punMember);
        };
        executor.schedule(unpunish, timeout, TimeUnit.SECONDS);
        event.reply(String.format("%s has been punished for `%d` seconds.", punMember.getAsMention(), timeout));
    }
}
