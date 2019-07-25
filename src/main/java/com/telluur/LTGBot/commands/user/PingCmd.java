package com.telluur.LTGBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.commands.UserCommand;

import java.util.Random;

/**
 * Simple ping-pong command
 *
 * @author Rick Fontein
 */

public class PingCmd extends UserCommand {
    public PingCmd(LTGBot ltgBot) {
        super(ltgBot);
        this.name = "ping";
        this.help = "pong!";
        this.guildOnly = false;
    }

    @Override
    public void handle(CommandEvent event) {
        String pong = puns[random.nextInt(puns.length)];
        event.reply(pong);
    }

    private Random random = new Random();
    private static final String[] puns = {
            "What do you serve but not eat? \n" +
                    "A ping pong ball. ",
            "What do you call a girl standing in the middle of a table tennis court? \n" +
                    "Annette ",
            "Why are fish not good at ping pong? \n" +
                    "They don't like getting close to the net. ",
            "What did one ping pong ball say to the other ping pong ball? \n" +
                    "\"See you round..\"",
            "Why are spiders great ping pong players?\n" +
                    "Cause they have great topspin.",
            "What happens when you use pickles for a ping pong game? \n" +
                    "You get a volley of the Dills. \n",
            "When does a ping pong player go to sleep? \n" +
                    "Around Tennish. ",
            "What's a horse's favorite sport? \n" +
                    "Stable Tennis. ",
            "My ping pong opponent was not happy with my serve. He kept returning it. ",
            "Ping Pong: 10% of the time hitting a ping pong ball, 90% of the time chasing the ball around the room. ",
            "Are you a ping pong table? Cuz you ping pong my balls. ",
            "Stop staring at my \"Balls of Fury\". "
    };
}
