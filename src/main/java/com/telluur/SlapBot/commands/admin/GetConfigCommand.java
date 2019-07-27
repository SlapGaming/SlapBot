package com.telluur.SlapBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.AdminCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;


/**
 * Command that displays the configuration of the bot.
 *
 * @author Rick Fontein
 */

public class GetConfigCommand extends AdminCommand {
    public GetConfigCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "config";
        this.help = "Displays the loaded configuration file (excluding token).";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        Guild guild = slapBot.getGuild();
        TextChannel tc = slapBot.getTextChannel();

        User owner = slapBot.getOwner();
        Role admin = slapBot.getAdminRole();
        Role mod = slapBot.getModeratorRole();

        String prefix = slapBot.getPrefix();
        String altPrefix = slapBot.getAltPrefix();


        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Bot Configuration")
                .setColor(Color.RED)
                .setDescription("The bot's settings as defined in config.yaml (Excluding tokens).")
                .addField("Guild", guild.getName(), true)
                .addField("Text Channel", tc.getAsMention(), true)
                .addBlankField(true)
                .addField("Bot Owner", owner.getName(), true)
                .addField("Admins", admin.getName(), true)
                .addField("Moderators", mod.getName(), true)
                .addField("Command Prefix", String.format("`%s` or `%s`", prefix, altPrefix), true);
        event.reply(eb.build());
    }
}
