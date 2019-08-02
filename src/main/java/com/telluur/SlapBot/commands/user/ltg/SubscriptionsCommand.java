package com.telluur.SlapBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.menu.Paginator;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import com.telluur.SlapBot.ltg.LTGHandler;
import com.telluur.SlapBot.ltg.storage.StorageHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.PermissionException;

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
    private final Paginator.Builder textChatBuilder;
    private final EmbedBuilder privateChatBuilder;

    public SubscriptionsCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "info";
        this.aliases = new String[]{"subscriptions", "subs"};
        this.arguments = "<?@role|?@user>";
        this.help = "Displays subscription info of a LTG role or user, defaults to your own subscriptions.";
        this.guildOnly = false;

        textChatBuilder = new Paginator.Builder()
                .setColor(LTGHandler.getCOLOR())
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
                .setEventWaiter(slapBot.getEventWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
        privateChatBuilder = new EmbedBuilder()
                .setColor(LTGHandler.getCOLOR());
    }

    @Override
    public void handle(CommandEvent event) {
        Member issuer = event.getMember();

        if (issuer == null && slapBot.getGuild().isMember(event.getAuthor())) {
            issuer = slapBot.getGuild().getMember(event.getAuthor());
        } else {
            event.replyError(String.format("You need to be a member of `%s`", slapBot.getGuild().getName()));
        }

        //Handle no argument
        if (event.getArgs().isEmpty()) {
            memberDisplay(event, issuer);
            return;
        }

        //Handle role mention
        String[] parts = event.getArgs().split("\\s+");

        List<Role> mentionedRoles = FinderUtil.findRoles(parts[0], slapBot.getGuild());
        StorageHandler handler = slapBot.getLtgHandler().getStorageHandler();
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
        List<Member> mentionedMembers = FinderUtil.findMembers(parts[0], slapBot.getGuild());
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
        StorageHandler handler = slapBot.getLtgHandler().getStorageHandler();
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
            event.replySuccess(String.format("`%s` does not have any subscriptions.", member.getEffectiveName()));
        } else {
            String replyHeader = String.format("**Looking-to-game subscriptions of `%s`**", member.getEffectiveName());
            //noinspection DuplicatedCode
            createReply(event, games, replyHeader);
        }
    }

    private void roleDisplay(CommandEvent event, Role role) {
        String[] subscribers = slapBot.getGuild().getMembersWithRoles(role)
                .stream()
                .map(member -> String.format("`%-50s`", member.getEffectiveName()))
                .sorted()
                .toArray(String[]::new);

        if (subscribers.length <= 0) {
            event.replySuccess(String.format("`%s` does not have any subscribers.", role.getName()));
        } else {
            String replyHeader = String.format("**Looking-to-game subscribers of `%s`, total subscriber count: `%d`.**", role.getName(), subscribers.length);
            createReply(event, subscribers, replyHeader);
        }
    }

    private void createReply(CommandEvent event, String[] subscribers, String replyHeader) {
        switch (event.getChannel().getType()) {
            case TEXT:
                textChatBuilder
                        .setText(replyHeader)
                        .setItems(subscribers)
                        .build()
                        .display(event.getChannel());
                break;
            case PRIVATE:
                MessageEmbed me = privateChatBuilder
                        .setDescription(String.join("\r\n", subscribers))
                        .build();
                event.reply(replyHeader);
                event.reply(me);
                break;
            default:
                event.replyError("Could not display message in this channel.");
                break;
        }
    }
}