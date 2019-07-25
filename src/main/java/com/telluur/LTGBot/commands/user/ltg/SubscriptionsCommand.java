package com.telluur.LTGBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.UserCommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

/**
 * Shows the users information about their/a user's/a LTG role's subscribers.
 *
 * @author Rick Fontein
 */

public class SubscriptionsCommand extends UserCommand {

    public SubscriptionsCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "subscriptions";
        this.aliases = new String[]{"subs"};
        this.arguments = "<?@role|?@user>";
        this.help = "Displays subscription info of a LTG role or user, defaults to your own subscriptions.";
    }

    @Override
    public void handle(CommandEvent event) {
        //Delete message as it may contain a mention
        event.getMessage().delete().queue();

        //Handle no argument
        if (event.getArgs().isEmpty()) {
            //TODO default: authors subs
            return;
        }

        //Handle role mention
        String[] parts = event.getArgs().split("\\s+");

        List<Role> mentionedRoles = FinderUtil.findRoles(parts[0], event.getGuild());
        if (mentionedRoles.size() == 1) {
            Role role = mentionedRoles.get(0);
            //TODO roles subs.
            return;
        }

        //Handle member mention
        List<Member> mentionedMembers = FinderUtil.findMembers(parts[0], ltgBot.getGuild());
        if (mentionedMembers.size() == 1){
            Member member = mentionedMembers.get(0);
            //TODO users subs.
            return;
        }

        //This should only be reached when invalid arguments are given.
        event.replyError("Please include a single valid `<@role>` or `<@user>`. Including no arguments displays your own subscriptions.");
    }
}
