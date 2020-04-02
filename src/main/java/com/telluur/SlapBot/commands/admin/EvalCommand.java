package com.telluur.SlapBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.AdminCommand;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Scripting command
 * Evaluates nashorn code with some extra bindings
 *
 * @author Rick Fontein
 */

public class EvalCommand extends AdminCommand {
    private final SlapBot bot;

    public EvalCommand(SlapBot bot) {
        super(bot);
        this.bot = bot;
        this.name = "eval";
        this.help = "evaluates nashorn code, available bindings: bot, event, jda, guild, channel.";
        this.guildOnly = false;
    }


    @Override
    public void handle(CommandEvent event) {
        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
        se.put("bot", bot);
        se.put("event", event);
        se.put("jda", event.getJDA());
        se.put("guild", event.getGuild());
        se.put("tx", event.getChannel());
        try {
            event.replySuccess(String.format("Evaluated Successfully:\r\n```\r\n%s```", se.eval(event.getArgs())));
        } catch (Exception e) {
            event.replyError(String.format("An exception was thrown:\r\n```\r\n%s```", e.getMessage()));
        }
    }
}