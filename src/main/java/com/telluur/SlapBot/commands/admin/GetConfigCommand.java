package com.telluur.LTGBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.AdminCommand;
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
    public GetConfigCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "config";
        this.help = "Displays the loaded configuration file (excluding token).";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        Guild guild = ltgBot.getGuild();
        TextChannel tc = ltgBot.getTextChannel();

        User owner = ltgBot.getOwner();
        Role admin = ltgBot.getAdminRole();
        Role mod = ltgBot.getModeratorRole();

        String prefix = ltgBot.getPrefix();
        String altPrefix = ltgBot.getAltPrefix();


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
