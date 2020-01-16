package com.telluur.SlapBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import com.telluur.SlapBot.features.lan.LanStorageHandler;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.IOException;

/**
 * Displays date and time remaining to next slap lan event
 *
 * @author Rick Fontein
 */

public class LanCommand extends UserCommand {
    private static final String MESSAGE = "**%s** (%s)\r\nTime remaining: %s";
    private static final String DATE_FMT = "EEEE d MMMM yyyy - HH:mm z";
    private static final PeriodFormatter REMAINING_FMT = new PeriodFormatterBuilder()
            .printZeroNever()
            .appendYears()
            .appendSuffix(" year, ", " years, ")
            .appendMonths()
            .appendSuffix(" month, ", " months, ")
            .printZeroRarelyLast()
            .appendDays()
            .appendSuffix(" day, ", " days, ")
            .appendHours()
            .appendSuffix(" hour, ", " hours, ")
            .appendMinutes()
            .appendSuffix(" minute.", " minutes.")
            .toFormatter();


    public LanCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "lan";
        this.aliases = new String[]{"slan"};
        this.help = "Displays time to next Slap LAN Event";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        try {
            LanStorageHandler lanStorageHandler = slapBot.getLanStorageHandler();

            String eventName = slapBot.getLanStorageHandler().getEventName();
            DateTime eventDate = slapBot.getLanStorageHandler().getDate().withZone(SlapBot.TIME_ZONE);

            DateTime current = new DateTime();
            Period period = new Period(current.getMillis(), lanStorageHandler.getDate().getMillis());

            event.reply(String.format(MESSAGE, eventName, eventDate.toString(DATE_FMT), period.toString(REMAINING_FMT)));


        } catch (IOException e) {
            event.replyError("An IO exceoption has occurred.");
        }
    }
}
