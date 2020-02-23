package com.telluur.SlapBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.AdminCommand;
import com.telluur.SlapBot.features.slapevents.SlapEvent;
import com.telluur.SlapBot.features.slapevents.SlapEventStorageHandler;
import com.telluur.SlapBot.features.slapevents.SlapEventUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * This abomination allows you to manage the slap events.
 *
 * @author Rick Fontein
 */

public class EventManageCommand extends AdminCommand {
    private static final Logger el = LoggerFactory.getLogger("EVENTS");
    private static final String FORMAT = "HH:mm z - EEEE d MMMM yyyy";

    private static final String IO_REPLY = "Uh oh. An IO exception has occurred. Failed to update `%s`.";
    private final String HELP;
    private final String NOT_ENOUGH_PARAMS;


    public EventManageCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "eventmanage";
        this.aliases = new String[]{"em"};
        this.arguments = "[help|status|list|info|create|delete|prune|edit|notify] <+params>";
        this.help = "Manage slap events";
        this.guildOnly = false;

        this.NOT_ENOUGH_PARAMS = String.format("Not enough arguments given. Use `%s%s help` to see the help page.", slapBot.getPrefix(), this.name);
        this.HELP = String.format("\n**Event Management Commands**\n" +
                "```markdown\n" +
                "%1$s help - displays this message\n" +
                "%1$s status - displays system and local time\n" +
                "\n" +
                "%1$s list - lists events by their <id>\n" +
                "%1$s info <id> - display the event with <id>\n" +
                "%1$s create <id> - creates event with <id>\n" +
                "%1$s delete <id> - deletes event with <id>\n" +
                "%1$s prune <id> - deletes events with begin/end date in the past.\n" +
                "%1$s edit <id> description <value> - sets <value>\n" +
                "%1$s edit <id> begin <ISO> - sets <ISO>\n" +
                "%1$s edit <id> end <ISO> - sets <ISO>\n" +
                "\n" +
                "%1$s notify <id> list - lists notify ISO dates for <id>\n" +
                "%1$s notify <id> add <ISO> - adds a notification at <ISO> for event <id>\n" +
                "%1$s notify <id> remove <ISO> - removes a notification at <ISO> for event <id>\n" +
                "%1$s notify <id> clear - clears all notification for <id>\n" +
                "\n" +
                "<ISO> expects a valid ISO 8601 string as argument, e.g. 2020-12-31T23:59:59+00:00\n" +
                "```\n" +
                "To create an event, you need to create a new event with `create`, then set the indivual fields with `edit`.\n\n" +
                "An event is only considered valid when `<description>`, `<begin>` and `<end>` are set and `<begin>` is chronologically before `<end>`.\n" +
                "__Invalid events will not be visible in the normal user commands__, you can use `list` and `info` to view these events.", this.name);
    }

    @Override
    public void handle(CommandEvent event) {
        SlapEventStorageHandler storage = slapBot.getSlapEventStorageHandler();
        String[] params = event.getArgs().split("\\s+", 4);

        if (params.length >= 1) {
            switch (params[0].toLowerCase()) {
                case "help":
                    event.reply(HELP);
                    return;

                case "status":
                    DateTime current = new DateTime();
                    event.reply(String.format("__**Current Time**__\r\n UK: `%s`\r\n System: `%s`\r\n",
                            current.withZone(SlapBot.TIME_ZONE).toString(FORMAT),
                            current.toString(FORMAT)
                    ));
                    return;

                case "list":
                    List<String> ids = storage.getEventIDs();

                    if (ids.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Event IDs:\r\n```\r\n");
                        ids.forEach(s -> {
                            String descr = null;
                            try {
                                descr = storage.getEventByID(s).getDescription();
                            } catch (IOException ignored) {
                            }
                            descr = (descr == null) ? "[Description not set]" : descr;
                            sb.append(String.format("%s | %s \r\n", s, descr));
                        });
                        sb.append("```");
                        event.reply(sb.toString());
                    } else {
                        event.reply("No events found.");
                    }
                    return;

                case "info":
                    if (params.length >= 2) {
                        String id = params[1];
                        if (storage.hasEventByID(id)) {
                            try {
                                SlapEvent myEvent = storage.getEventByID(id);
                                String begin = myEvent.getStart();
                                String end = myEvent.getEnd();
                                event.reply(String.format("```\r\n" +
                                                "ID          | %s\r\n" +
                                                "Description | %s\r\n" +
                                                "Begin ISO   | %s\r\n" +
                                                "End ISO     | %s\r\n" +
                                                "```",
                                        id, myEvent.getDescription(), begin, end));
                            } catch (IOException e) {
                                event.replyError(String.format(IO_REPLY, id));
                                el.error(e.getMessage());
                            }
                        } else {
                            event.replyError(String.format("Event with id `%s` does not exist.", id));
                        }
                    }
                    return;

                case "create":
                    if (params.length >= 2) {
                        String id = params[1];
                        if (storage.hasEventByID(id)) {
                            event.replyError(String.format("Event with id `%s` already exists.", id));
                        } else {
                            try {
                                storage.setEventByID(id, new SlapEvent());
                                event.reply(String.format("Event with id `%s` created.", id));
                                el.info(String.format("Created event with ID: %s", id));
                            } catch (IOException e) {
                                event.replyError(String.format(IO_REPLY, id));
                                el.error(e.getMessage());
                            }
                        }
                    } else {
                        event.replyError(NOT_ENOUGH_PARAMS);
                    }
                    return;

                case "delete":
                    if (params.length >= 2) {
                        String id = params[1];
                        if (storage.hasEventByID(id)) {
                            try {
                                storage.deleteEventByID(id);
                                event.reply(String.format("Event with id `%s` deleted.", id));
                                el.info(String.format("Deleted event with ID: %s", id));
                            } catch (IOException e) {
                                event.replyError(String.format(IO_REPLY, id));
                                el.error(e.getMessage());
                            }
                        } else {
                            event.replyError(String.format("Event with id `%s` does not exist.", id));
                        }
                    } else {
                        event.replyError(NOT_ENOUGH_PARAMS);
                    }
                    return;

                case "prune":
                    //TODO Implement prune
                    event.reply("PRUNE | Not yet implemented...");
                    return;

                case "edit":
                    if (params.length >= 4) {
                        String id = params[1];
                        if (storage.hasEventByID(id)) {
                            try {
                                SlapEvent myEvent = storage.getEventByID(id);
                                switch (params[2].toLowerCase()) {
                                    case "description":
                                        String desc = params[3];
                                        myEvent.setDescription(desc);
                                        storage.setEventByID(id, myEvent);
                                        String msg = String.format("Updated description of `%s` to `%s`", id, desc);
                                        event.reply(msg);
                                        el.info(msg);
                                        if (!SlapEventStorageHandler.isValidEvent(myEvent)) {
                                            event.reply(String.format("**Event** `%s` **is not valid/incomplete, and won't be visible to users.**", id));
                                        }
                                        return;

                                    case "begin":
                                        String beginISO = params[3];
                                        DateTime startDateTime = new DateTime(beginISO).withZone(SlapBot.TIME_ZONE);
                                        myEvent.setStart(SlapEventUtil.toISOStringOrNull(startDateTime));
                                        storage.setEventByID(id, myEvent);
                                        event.reply(String.format("Updated start date of `%s` to `%s`", id, startDateTime.toString(FORMAT)));
                                        el.info(String.format("Updated begin of `%s` to `%s`", id, beginISO));
                                        if (!SlapEventStorageHandler.isValidEvent(myEvent)) {
                                            event.reply(String.format("**Event** `%s` **is not valid/incomplete, and won't be visible to users.**", id));
                                        }
                                        return;

                                    case "end":
                                        String endISO = params[3];
                                        DateTime endDateTime = new DateTime(endISO).withZone(SlapBot.TIME_ZONE);
                                        myEvent.setEnd(SlapEventUtil.toISOStringOrNull(endDateTime));
                                        storage.setEventByID(id, myEvent);
                                        event.reply(String.format("Updated end date of `%s` to `%s`", id, endDateTime.toString(FORMAT)));
                                        el.info(String.format("Updated end of `%s` to `%s`", id, endISO));
                                        if (!SlapEventStorageHandler.isValidEvent(myEvent)) {
                                            event.reply(String.format("**Event** `%s` **is not valid/incomplete, and won't be visible to users.**", id));
                                        }
                                        return;

                                    default:
                                        event.replyError(String.format("Unknown subcommand of `edit`. Use `%s%s help` to see the help page.", slapBot.getPrefix(), this.name));
                                        return;
                                }
                            } catch (IOException e) {
                                event.replyError(String.format(IO_REPLY, id));
                                el.error(e.getMessage());
                            } catch (IllegalArgumentException e) {
                                event.replyError(String.format("Provided an invalid date/time string: `%s`. " +
                                        "\r\nPlease provide a valid `ISO 8601` string.", params[3]));
                            }
                        } else {
                            event.replyError(String.format("Event with id `%s` does not exist.", id));
                        }
                    } else {
                        event.replyError(NOT_ENOUGH_PARAMS);
                    }
                    return;

                case "notify":
                    //TODO Implement notify
                    event.reply("NOTIFY | Not yet implemented...");
                    return;

                default:
                    event.replyError(String.format("Unknown subcommand. Use `%s%s help` to see the help page.", slapBot.getPrefix(), this.name));
                    return;
            }
        } else {
            event.replyError(NOT_ENOUGH_PARAMS);
            return;
        }
    }
}
