package com.telluur.SlapBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.AdminCommand;
import com.telluur.SlapBot.features.lan.LanStorageHandler;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * TODO add class description
 *
 * @author Rick Fontein
 */

public class SetLanCommand extends AdminCommand {
    private static final String HELP = "Updates the LAN event." +
            "\r\n See `setlan name` for event name help." +
            "\r\n See `setlan date` for event date help.";
    private static final String HELP_NAME = "__**Name help**__" +
            "\r\nThis command sets the name of the next event.";
    private static final String HELP_DATE = "__**Date help**__" +
            "\r\n*Programming across time zones sucks.*" +
            "\r\nThis command expects a valid `ISO 8601` string as argument, e.g. `2020-12-31T23:59:59+00:00`" +
            "\r\nAll member facing commands display UK time by default.";
    private static final String IO_REPLY = "Uh oh. An IO exception has occurred. Failed to update `%s`.";
    private static final String FORMAT = "HH:mm z - EEEE d MMMM yyyy";

    public SetLanCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "setlan";
        this.aliases = new String[]{"updatelan"};
        this.arguments = "[name|date] <event name|ISO8601 datetime>";
        this.help = "Updates the name or date for the next slap lan.";
        this.guildOnly = false;
    }

    private static String currentTimeMessage() {
        DateTime current = new DateTime();
        return String.format("__**Current Time**__\r\n UK: `%s`\r\n System: `%s`\r\n",
                current.withZone(SlapBot.TIME_ZONE).toString(FORMAT),
                current.toString(FORMAT)
        );
    }

    @Override
    public void handle(CommandEvent event) {
        LanStorageHandler lanStorageHandler = slapBot.getLanStorageHandler();

        String[] parts = event.getArgs().split("\\s+", 2);

        // I wish java had FP style pattern matching...
        if (parts.length == 1 && parts[0].toLowerCase().equals("name")) {
            event.reply(HELP_NAME);
        } else if (parts.length == 1 && parts[0].toLowerCase().equals("date")) {
            event.reply(String.format("%s\r\n\r\n%s", HELP_DATE, currentTimeMessage()));
        } else if (parts.length == 2 && parts[0].toLowerCase().equals("name")) {
            //Handle name update.
            String lanEvent = parts[1];
            try {
                lanStorageHandler.setEventName(lanEvent);
                event.reply(String.format("Set event to `%s`.", lanEvent));
            } catch (IOException e) {
                event.reply(String.format(IO_REPLY, "name"));
            }
        } else if (parts.length == 2 && parts[0].toLowerCase().equals("date")) {
            //Handle date update
            String ISODate = parts[1];
            try {
                DateTime dt = new DateTime(ISODate);
                lanStorageHandler.setDate(dt);
                event.reply(String.format("__**Updated Event Date**__ \r\nUK: `%s` \r\nSystem: `%s` \r\n\r\n%s",
                        dt.withZone(SlapBot.TIME_ZONE).toString(FORMAT),
                        dt.toString(FORMAT),
                        currentTimeMessage()));
            } catch (IOException e) {
                event.reply(String.format(IO_REPLY, "name"));
            } catch (IllegalArgumentException e) {
                event.replyError(String.format("Provided an invalid date/time string: `%s`. " +
                                "\r\nPlease provide a valid `ISO 8601` string. " +
                                "\r\n\r\n%s" +
                                "\r\n\r\n%s",
                        ISODate, HELP_DATE, currentTimeMessage())
                );
            }
        } else {
            event.reply(HELP);
        }
    }
}
