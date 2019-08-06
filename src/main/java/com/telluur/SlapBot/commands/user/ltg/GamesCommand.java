package com.telluur.SlapBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import com.telluur.SlapBot.ltg.LTGHandler;
import com.telluur.SlapBot.ltg.storage.StorageHandler;
import com.telluur.SlapBot.ltg.storage.StoredGame;
import com.telluur.SlapBot.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Shows an alphabetical list of all LTG games including subscriber count
 *
 * @author Rick Fontein
 */

public class GamesCommand extends UserCommand {
    private static final char SPACE = '\u00A0'; //No break space character, cause discord collapses normal ones.
    private static final String REPLY_HEADER = "**Looking-to-game roles**";
    private final Paginator.Builder textChatBuilder;
    private final EmbedBuilder privateChatBuilder;

    public GamesCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "games";
        this.help = "Shows an alphabetical list of all LTG games";
        this.guildOnly = false;
        textChatBuilder = new Paginator.Builder()
                .setText(REPLY_HEADER)
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
        Guild g = slapBot.getGuild();
        StorageHandler handler = slapBot.getLtgHandler().getStorageHandler();

        /*
        Get gameSnowflakes from storage
        Create a hashmap with <Snowflake, Storedgame>
        Filter out invalid games
        Map to string representation in paginator
        Sort alphabetically
        Collect to array for varargs paginator
         */
        String[] games = handler.getGameSnowflakes()
                .stream()
                .map(s -> {
                    try {
                        return new HashMap.SimpleEntry<Role, StoredGame>(g.getRoleById(s), handler.getGameBySnowflake(s));
                    } catch (IOException e) {
                        return new HashMap.SimpleEntry<Role, StoredGame>(null, null);
                    }
                })
                .filter(entry -> entry.getKey() != null)
                .map(entry -> String.format(
                        "`%-6s | %-40s | %2d subs`",
                        entry.getValue().getAbbreviation(),
                        entry.getValue().getFullName(),
                        g.getMembersWithRoles(entry.getKey()).size())
                        .replace(' ', SPACE))
                .sorted()
                .toArray(String[]::new);

        //No games in storage, dont built paginator.
        if (games.length == 0) {
            event.replyError("No LTG roles found.");
            return;
        }
        switch (event.getChannel().getType()) {
            case TEXT:
                textChatBuilder
                        .setItems(games)
                        .build()
                        .display(event.getChannel());
                break;
            case PRIVATE:
                event.reply(REPLY_HEADER);
                for (String message : EmbedUtil.splitDiscordLimit(games)) {
                    MessageEmbed me = privateChatBuilder
                            .setDescription(message)
                            .build();
                    event.reply(me);
                }
                break;
            default:
                event.replyError("Could not display message in this channel.");
                break;
        }
    }
}
