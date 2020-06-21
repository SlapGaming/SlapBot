package com.telluur.SlapBot.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.UserCommand;

import java.util.Random;

public class RollCommand extends UserCommand {
    private final Random rand = new Random();

    public RollCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "roll";
        this.help = "Roll a random number between 1 and <bound>, defaults to 1-100";
        this.arguments = "<bound> <?excluded @mentioned users>";
        this.guildOnly = true;
    }

    @Override
    public void handle(CommandEvent event) {
        int bound;
        if ("".equals(event.getArgs())) {
            bound = 100;
        } else {
            try {
                bound = Integer.parseInt(event.getArgs().split("\\s+")[0]);
            } catch (NumberFormatException e) {
                if (event.getAuthor().getIdLong() == 162890083735830529L) {
                    event.replyError("Fuck off Sam.");
                } else {
                    event.reply("Something went wrong. ¯\\_(ツ)_/¯");
                }
                return;
            }
        }

        int r = rand.nextInt(bound + 1);
        event.reply(String.format("Computer says `%d`.", r));
        switch (r) {
            case 4:
                event.reply("https://imgs.xkcd.com/comics/random_number.png ");
                break;
            case 69:
                event.reply("noice. ( ͡° ͜ʖ ͡°)");
                break;
        }
    }
}
