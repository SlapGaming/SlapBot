package com.telluur.SlapBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import com.telluur.SlapBot.features.lan.LanStorageHandler;
import com.vdurmont.emoji.EmojiParser;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
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
            .printZeroRarelyLast()
            .appendYears()
            .appendSuffix(" year, ", " years, ")
            .appendMonths()
            .appendSuffix(" month, ", " months, ")
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
            DateTime lan = lanStorageHandler.getDate();
            Period period = new Period(current, lan, PeriodType.yearMonthDayTime());

            if (lan.getMillis() - current.getMillis() >= 0) {
                event.reply(String.format(MESSAGE, eventName, eventDate.toString(DATE_FMT), period.toString(REMAINING_FMT)));
            } else {
                event.reply(EmojiParser.parseToUnicode("SLAP has no future events planned... :sob:"));
            }
        } catch (IOException e) {
            event.replyError("An IO exceoption has occurred.");
        }
    }
}
