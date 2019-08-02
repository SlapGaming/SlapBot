package com.telluur.SlapBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import com.telluur.SlapBot.pun.PunException;
import com.telluur.SlapBot.pun.PunHandler;
import com.telluur.SlapBot.pun.Punishment;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;
import java.util.Optional;

/**
 * Pun-ishes a user
 *
 * @author Rick Fontein
 */


public class PunCommand extends UserCommand {

    private PunHandler punHandler;

    public PunCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "pun";
        this.aliases = new String[]{"fpun"};
        this.help = "pun-ish a user without vote. ";
        this.arguments = "<@user> <?seconds=10>";
        this.guildOnly = true;

        this.punHandler = new PunHandler(slapBot);
    }

    @Override
    public void handle(CommandEvent event) {
        try {
            Punishment punishment = parseArguments(event);
            checkTimeoutBounds(event, punishment);
            event.reply(" pun-ishment...");
            punHandler.punish(event, punishment);
        } catch (PunException e) {
            event.replyError(e.getMessage());
        }
    }

    private Punishment parseArguments(CommandEvent event) throws PunException {
        //Check if user is in a voice channel
        if (!event.getMember().getVoiceState().inVoiceChannel()) {

            throw new PunException("You must be in a voice channel to use this command.");
        }

        //Check if sufficient arguments are supplied
        if ("".equals(event.getArgs())) {
            throw new PunException("No arguments, minimal: " + arguments);
        }

        String[] args = event.getArgs().split("\\s+");

        //Get users from calling voice channel
        List<Member> vcMembers = event.getGuild().getMember(event.getAuthor()).getVoiceState().getChannel().getMembers();

        //Parse first <
        Optional<Member> maybeMember = vcMembers.stream()
                .filter(member -> member.getUser().getAsMention().equals(args[0].replaceAll("!", "")))
                .findFirst();

        Member punMember;
        if (maybeMember.isPresent()) {
            punMember = maybeMember.get();
        } else {
            throw new PunException("First argument should be <@user> currently in your voice channel");
        }

        int timeout = 10;
        if (args.length > 1) {
            //Try to parse first argument and check team bounds
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new PunException("Second argument should be a number.", e);
            }
        }

        //If a user tries to punish a bot, punish them instead.
        if (punMember.getUser().isBot()) {
            event.reply(event.getSelfMember().getAsMention() + " uses reflect. It's super effective.");
            punMember = event.getMember();
        }

        //If a user tries to punish a the owner, punish them instead.
        if (punMember.getUser().equals(slapBot.getOwner())) {
            event.reply(slapBot.getOwner().getAsMention() + " uses reflect. It's super effective.");
            punMember = event.getMember();
        }

        return new Punishment(punMember, timeout);
    }


    private void checkTimeoutBounds(CommandEvent event, Punishment punishment) throws PunException {
        int timeout = punishment.getTimeout();
        if (timeout < 10) {
            throw new PunException("The Discord gods will tickle you in inappropriate places if you spam their API. Minimal timeout 10 seconds.");
        } else if (timeout > 300) {
            throw new PunException(event.getAuthor().getAsMention() + ", you twat. Trying to timeout someone for over 5 minutes.");
        }
    }
}
