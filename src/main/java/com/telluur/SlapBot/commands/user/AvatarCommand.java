package com.telluur.SlapBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

public class AvatarCommand extends UserCommand {
    public AvatarCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "avatar";
        this.help = "Enlarges a user's profile picture";
        this.arguments = "<@user>";
        this.guildOnly = true;

    }

    @Override
    public void handle(CommandEvent event) {
        String arg = event.getArgs();

        if (arg.isEmpty()) {
            event.replyError("Whose avatar do you want, bellend?");
            return;
        }

        List<Member> memberList = FinderUtil.findMembers(arg, slapBot.getGuild());
        if (memberList.size() < 1) {
            event.replyError(EmojiParser.parseToUnicode("Either I am retarded, or you tried to mention a user not on this server. :shrug:"));
            return;
        }
        if (memberList.size() > 1) {
            event.replyError(EmojiParser.parseToUnicode("Found multiple users matching your query. :thinking: Please be more specific."));
            return;
        }

        Member m = memberList.get(0);
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(SlapBot.COLOR)
                .setTitle(String.format("%s's avatar", m.getEffectiveName()))
                .setImage(String.format("%s%s", m.getUser().getEffectiveAvatarUrl(), "?size=2048"));
        event.reply(builder.build());
    }
}
