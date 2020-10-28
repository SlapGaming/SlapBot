package com.telluur.SlapBot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.commands.abstractions.AdminCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.Random;

public class FakeHaloweenCommand extends AdminCommand {
    public static final String TX_ID = "304728836883349516";
    public static final String[] IMAGES = new String[]{
            "https://cdn.discordapp.com/halloween-bot/Witch.png",
            "https://cdn.discordapp.com/halloween-bot/Goblin.png",
            "https://cdn.discordapp.com/halloween-bot/Robot.png",
            "https://cdn.discordapp.com/halloween-bot/Wumpus-Dino.png",
            "https://cdn.discordapp.com/halloween-bot/Werewolf.png",
            "https://cdn.discordapp.com/halloween-bot/Rat.png"
    };
    public static final String[] TRICK_OR_TREAT = new String[]{
            "h!trick",
            "h!treat"
    };
    private static final Random random = new Random();


    public FakeHaloweenCommand(SlapBot slapBot) {
        super(slapBot);
        this.name = "haloween";
        this.aliases = new String[]{"h"};
        this.help = "fucking them no life pricks";
        this.guildOnly = false;
        this.echoUserIssue = false;
    }


    @Override
    public void handle(CommandEvent event) {
        TextChannel txChannel = event.getJDA().getTextChannelById(TX_ID);
        if (txChannel != null) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(new Color(114, 137, 218))
                    .setTitle("A trick-or-treater has stopped by!")
                    .setDescription(String.format("Open the door and greet them with %s", TRICK_OR_TREAT[random.nextInt(TRICK_OR_TREAT.length)]))
                    .setImage(IMAGES[random.nextInt(IMAGES.length)]);
            txChannel.sendMessage(eb.build()).queue();
        }
    }
}
