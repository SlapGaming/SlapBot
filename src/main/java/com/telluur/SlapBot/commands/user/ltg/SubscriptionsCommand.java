package com.telluur.LTGBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.menu.Paginator;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.UserCommand;
import com.telluur.LTGBot.ltg.storage.StorageHandler;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Shows the users information about their/a user's/a LTG role's subscribers.
 *
 * @author Rick Fontein
 */

public class SubscriptionsCommand extends UserCommand {
    private static final char SPACE = '\u00A0'; //No break space character, cause discord collapses normal ones.
    private final Paginator.Builder builder;

    public SubscriptionsCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "subscriptions";
        this.aliases = new String[]{"subs", "info"};
        this.arguments = "<?@role|?@user>";
        this.help = "Displays subscription info of a LTG role or user, defaults to your own subscriptions.";

        builder = new Paginator.Builder()
                .setColor(new Color(26, 188, 156))
                .setColumns(1)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException ignore) {
                    }
                })
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(false)
                .showPageNumbers(true)
                .setEventWaiter(ltgBot.getEventWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    public void handle(CommandEvent event) {
        //Handle no argument
        if (event.getArgs().isEmpty()) {
            memberDisplay(event, event.getMember());
            return;
        }

        //Handle role mention
        String[] parts = event.getArgs().split("\\s+");

        List<Role> mentionedRoles = FinderUtil.findRoles(parts[0], event.getGuild());
        StorageHandler handler = ltgBot.getLtgHandler().getStorageHandler();
        if (mentionedRoles.size() == 1) {
            Role role = mentionedRoles.get(0);
            if (handler.hasGameBySnowflake(role.getId())) {
                roleDisplay(event, role);
            } else {
                event.replyError(String.format("`%s` is not a looking-to-game role.", role.getName()));
            }
            return;
        }

        //Handle member mention
        List<Member> mentionedMembers = FinderUtil.findMembers(parts[0], ltgBot.getGuild());
        if (mentionedMembers.size() == 1) {
            Member member = mentionedMembers.get(0);
            memberDisplay(event, member);
            return;
        }

        //This should only be reached when invalid arguments are given.
        event.replyError("Please include a single valid `<@role>` or `<@user>`. Including no arguments displays your own subscriptions.");
    }

    private void memberDisplay(CommandEvent event, Member member) {
        List<String> discordRoles = member.getRoles().stream().map(Role::getId).collect(Collectors.toList());
        StorageHandler handler = ltgBot.getLtgHandler().getStorageHandler();
        String[] games = handler.getGameSnowflakes()
                .stream()
                .filter(discordRoles::contains)
                .map(s -> {
                    try {
                        return handler.getGameBySnowflake(s);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(game -> String.format("`%-6s | %-40s `", game.getAbbreviation(), game.getFullName())
                        .replace(' ', SPACE))
                .sorted()
                .toArray(String[]::new);


        if (games.length <= 0) {
            event.replySuccess(String.format("`%s` does not have any subscriptions.", member.getNickname()));
        } else {
            builder.setText(String.format("Looking-to-game subscriptions of `%s`", member.getNickname()))
                    .setItems(games)
                    .build()
                    .display(event.getChannel());
        }
    }

    private void roleDisplay(CommandEvent event, Role role) {
        String[] subscribers = ltgBot.getGuild().getMembersWithRoles(role)
                .stream()
                .map(member -> String.format("`%-50s`", member.getNickname()))
                .sorted()
                .toArray(String[]::new);

        if (subscribers.length <= 0) {
            event.replySuccess(String.format("`%s` does not have any subscribers.", role.getName()));
        } else {
            builder.setText(String.format("Looking-to-game subscribers of `%s`, total subscriber count: `%d`.", role.getName(), subscribers.length))
                    .setItems(subscribers)
                    .build()
                    .display(event.getChannel());
        }
    }
}
