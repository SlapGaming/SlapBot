package com.telluur.LTGBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.AdminCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;


/**
 * Command that displays the configuration of the bot.
 *
 * @author Rick Fontein
 */

public class GetConfigCommand extends AdminCommand {
    public GetConfigCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "config";
        this.help = "displays the loaded configuration file (excluding token)";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        StringBuilder sb = new StringBuilder("**-={Config file}=-**\r\n");

        Guild guild = ltgBot.getGuild();
        sb.append(String.format("**Guild:** %s <%s>\r\n", guild.getName(), guild.getId()));

        User owner = ltgBot.getOwner();
        sb.append(String.format("**Owner:** %s <%s>\r\n", owner.getName(), owner.getId()));

        Role admin = ltgBot.getAdminRole();
        sb.append(String.format("**Admin:** %s <%s>\r\n", admin.getName(), admin.getId()));

        Role mod = ltgBot.getModeratorRole();
        sb.append(String.format("**Moderator:** %s <%s>\r\n", mod.getName(), mod.getId()));

        TextChannel tc = ltgBot.getTextChannel();
        sb.append(String.format("**Text Channel:** %s <%s>\r\n", tc.getName(), tc.getId()));

        String prefix = ltgBot.getPrefix();
        String altPrefix = ltgBot.getAltPrefix();
        sb.append(String.format("**Prefix:** `%s` or `%s`\r\n", prefix, altPrefix));

        event.reply(sb.toString());
    }
}
