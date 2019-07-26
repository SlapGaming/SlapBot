package com.telluur.LTGBot.commands.user.ltg;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.UserCommand;
import com.telluur.LTGBot.ltg.storage.StorageHandler;
import com.telluur.LTGBot.ltg.storage.StoredGame;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Shows an alphabetical list of all LTG games including subscriber count
 *
 * @author Rick Fontein
 */

public class GamesCommand extends UserCommand {
    private final Paginator.Builder builder;
    private static final char SPACE = '\u00A0'; //No break space character, cause discord collapses normal ones.

    public GamesCommand(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "games";
        this.help = "Shows an alphabetical list of all LTG games";
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
        Guild g = ltgBot.getGuild();
        StorageHandler handler = ltgBot.getLtgHandler().getStorageHandler();

        /*
        Get gameSnowflakes from storage
        Create a hashmap with <Snowflake, Storedgame>
        Filter out invalid games
        Map to string representation in paginator
        Sort alphabetically
        Collect to array for varargs paginator
         */
        //Get gameSnowflakes from storage convert them to JDA Role objects
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

        builder.setText("Looking-to-game roles in this guild")
                .setItems(games)
                .build()
                .display(event.getChannel());
    }
}
