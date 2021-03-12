package com.telluur.SlapBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;
import com.telluur.SlapBot.features.slapevents.jpa.SlapEvent;
import com.telluur.SlapBot.features.slapevents.jpa.SlapEventRepository;
import com.vdurmont.emoji.EmojiParser;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.List;
import java.util.Optional;

/**
 * Displays date and time remaining to next slap lan event
 *
 * @author Rick Fontein
 */

public class EventCommand extends UserCommand {
    private static final String SHORT_DATE_FORMAT = "EEE dd MMM yyyy";
    private static final String LONG_DATE_FORMAT = "EEEE d MMMM yyyy - HH:mm z";
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
    private static final String NO_EVENT = EmojiParser.parseToUnicode("SLAP has no future events planned... :sob:");


    public EventCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "event";
        this.aliases = new String[]{"lan", "slan", "events"};
        this.help = "Displays time to next Slap Event";
        this.arguments = "<?all>";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        SlapEventRepository repository = slapBot.getSlapEventRepository();
        String[] params = event.getArgs().split("\\s+");

        if (params.length >= 1 && params[0].equalsIgnoreCase("")) {
            Optional<SlapEvent> optional = repository.getNextEvent();
            if (!optional.isPresent()) {
                event.reply(NO_EVENT);
            } else {
                SlapEvent nextEvent = optional.get();
                DateTime now = new DateTime().withZone(SlapBot.TIME_ZONE);
                DateTime begin = nextEvent.getStart().withZone(SlapBot.TIME_ZONE);
                DateTime end = nextEvent.getEnd().withZone(SlapBot.TIME_ZONE);

                if (now.getMillis() - begin.getMillis() <= 0) {
                    //Future event
                    Period period = new Period(now, begin, PeriodType.yearMonthDayTime());
                    event.reply(EmojiParser.parseToUnicode(String.format("__**SLAP will be attending:**__\r\n" +
                                    "```\r\n" +
                                    "%s Event:           %s\r\n" +
                                    "%s Begins:          %s\r\n" +
                                    "%s Ends:            %s\r\n" +
                                    "%s Time till start: %s\r\n\r\n" +
                                    "%s" +
                                    "```\r\n" +
                                    "Type `%s%s all` to see all scheduled events.",
                            ":video_game:", nextEvent.getName(),
                            ":calendar:", begin.toString(LONG_DATE_FORMAT),
                            ":calendar:", end.toString(LONG_DATE_FORMAT),
                            ":timer_clock:", period.toString(REMAINING_FMT),
                            nextEvent.getDescription(),
                            slapBot.getPrefix(), this.name)));
                } else {
                    Period period = new Period(now, end, PeriodType.yearMonthDayTime());
                    event.reply(EmojiParser.parseToUnicode(String.format("__**SLAP is currently attending:**__\r\n" +
                                    "```\r\n" +
                                    "%s Event:         %s\r\n" +
                                    "%s Begins:        %s\r\n" +
                                    "%s Ends:          %s\r\n" +
                                    "%s Time till end: %s\r\n\r\n" +
                                    "%s" +
                                    "```\r\n" +
                                    "Type `%s%s all` to see all scheduled events.",
                            ":video_game:", nextEvent.getName(),
                            ":calendar:", begin.toString(LONG_DATE_FORMAT),
                            ":calendar:", end.toString(LONG_DATE_FORMAT),
                            ":timer_clock:", period.toString(REMAINING_FMT),
                            nextEvent.getDescription(),
                            slapBot.getPrefix(), this.name)));
                }
            }
        } else if (params.length >= 1 && params[0].equalsIgnoreCase("all")) {
            List<SlapEvent> events = repository.getFutureEvents();
            if (events.size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("__**All events:**__\r\n");
                sb.append("```\r\n");
                events.forEach(e -> sb.append(String.format(":calendar: %s: %s\r\n",
                        e.getStart().withZone(SlapBot.TIME_ZONE).toString(SHORT_DATE_FORMAT),
                        e.getName())));
                sb.append("```\r\n");
                event.reply(EmojiParser.parseToUnicode(sb.toString()));
            } else {
                event.reply(NO_EVENT);
            }
        } else {
            event.replyError(EmojiParser.parseToUnicode("That's not a valid subcommand, you melon. :watermelon:"));
        }
    }
}

