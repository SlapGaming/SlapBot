package com.telluur.SlapBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import com.telluur.SlapBot.features.ltg.LTGHandler;
import com.telluur.SlapBot.features.ltg.jpa.LTGRepository;
import com.telluur.SlapBot.features.ltg.listeners.QuickSubscribeListener;
import com.telluur.SlapBot.util.EmbedUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.PermissionException;

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

    public SubscriptionsCommand(SlapBot slapBot, EventWaiter eventWaiter) {
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
                .setEventWaiter(eventWaiter)
                .setTimeout(1, TimeUnit.MINUTES);
        privateChatBuilder = new EmbedBuilder()
                .setColor(LTGHandler.getCOLOR());
    }

    @Override
    public void handle(CommandEvent event) {
        Member issuer = event.getMember();

        if (issuer == null && slapBot.getGuild().isMember(event.getAuthor())) {
            if (slapBot.getGuild().isMember(event.getAuthor())) {
                issuer = slapBot.getGuild().getMember(event.getAuthor());
            } else {
                event.replyError(String.format("You need to be a member of `%s`", slapBot.getGuild().getName()));
                return;
            }
        }

        //Handle no argument
        if (issuer != null && event.getArgs().isEmpty()) {
            memberDisplay(event, issuer);
            return;
        }

        //Handle role mention
        String[] parts = event.getArgs().split("\\s+");

        List<Role> mentionedRoles = FinderUtil.findRoles(parts[0], slapBot.getGuild());
        LTGRepository repository = slapBot.getLtgHandler().getLtgRepository();
        if (mentionedRoles.size() == 1) {
            Role role = mentionedRoles.get(0);
            if (repository.hasId(role.getId())) {
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
        LTGRepository repository = slapBot.getLtgHandler().getLtgRepository();

        String[] games = repository.findAllIds()
                .stream()
                .filter(discordRoles::contains)
                .map(repository::findById)
                .filter(Objects::nonNull)
                .map(game -> String.format("`%-6s | %-40s `", game.getAbbreviation(), game.getFullName())
                        .replace(' ', SPACE))
                .sorted()
                .toArray(String[]::new);


        if (games.length <= 0) {
            event.replySuccess(String.format("`%s` does not have any subscriptions.", member.getEffectiveName()));
        } else {
            String replyHeader = String.format("**Looking-to-game subscriptions of `%s`**", member.getEffectiveName());
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
            createSubscribeButtonIfPossible(event, role);
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
                event.reply(replyHeader);
                for (String messages : EmbedUtil.splitDiscordLimit(subscribers)) {
                    MessageEmbed me = privateChatBuilder
                            .setDescription(messages)
                            .build();
                    event.reply(me);
                }
                break;
            default:
                event.replyError("Could not display message in this channel.");
                break;
        }
    }

    private void createSubscribeButtonIfPossible(CommandEvent event, Role role) {
        //Button is only possible in guild chat channels.
        if (event.getChannel().getType() == ChannelType.TEXT) {
            String msg = String.format("Click %s to subscribe to `%s`.", QuickSubscribeListener.SUBSCRIBE, role.getName());
            event.replySuccess(msg, m -> slapBot.getQuickSubscribeListener().addButton(m, role));
        }
    }
}
