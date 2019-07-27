package com.telluur.SlapBot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.telluur.SlapBot.SlapBot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract command as super class for all commands
 * Filters commands to the configured guild
 *
 * @author Rick Fontein
 */

public abstract class AbstractCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger("CMD");
    protected SlapBot slapBot;

    AbstractCommand(SlapBot slapBot) {
        this.slapBot = slapBot;
    }

    @Override
    protected void execute(CommandEvent event) {
        /*
        Scan and replace mentions in the command.
         */
        event.getMessage().delete().queue();
        String filteredCmd = Arrays.stream(event.getMessage().getContentRaw().split("\\s+"))
                .map(stringPart -> {
                    if (stringPart.startsWith("<")) {
                        List<Member> possibleMembers = FinderUtil.findMembers(stringPart, slapBot.getGuild());
                        List<Role> possibleRoles = FinderUtil.findRoles(stringPart, slapBot.getGuild());
                        if (possibleMembers.size() == 1) {
                            Member m = possibleMembers.get(0);
                            return String.format("`%s`", m.getEffectiveName());
                        } else if (possibleRoles.size() == 1) {
                            System.out.println("roles");
                            return String.format("`%s`", possibleRoles.get(0).getName());
                        } else {
                            return stringPart;
                        }
                    } else{
                        return stringPart;
                    }
                })
                .collect(Collectors.joining(" "));
        event.getChannel().sendMessage(String.format("`%s` issued command: %s", event.getMember().getEffectiveName(), filteredCmd)).queue();

    }

    boolean inValidGuildOrPrivate(CommandEvent event) {
        User user = event.getAuthor();
        String cmd = event.getMessage().getContentDisplay();
        Guild eventGuild = event.getGuild();
        Guild configGuild = slapBot.getGuild();

        if (eventGuild != null && !eventGuild.equals(configGuild)) {
            logger.info(String.format("<DENIED> %s: %s", user.getName(), cmd));
            event.replyError(String.format(
                    "This bot is locked to `%s`, and can only be used there or in private chat.",
                    configGuild.getName()
            ));
            return false;
        } else {
            return true;
        }
    }
}
