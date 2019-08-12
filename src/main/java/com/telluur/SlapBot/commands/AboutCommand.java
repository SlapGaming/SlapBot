package com.telluur.SlapBot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.telluur.SlapBot.Main;
import com.telluur.SlapBot.SlapBot;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;


/**
 * Command that displays the configuration of the bot.
 *
 * @author Rick Fontein
 */

public class AboutCommand extends Command {
    private static final String VERSION = Main.class.getPackage().getImplementationVersion() != null ?
            Main.class.getPackage().getImplementationVersion() : "DEV";
    private final SlapBot slapBot;

    public AboutCommand(SlapBot slapBot) {
        this.slapBot = slapBot;
        this.name = "about";
        this.aliases = new String[]{"config", "settings"};
        this.help = "About this bot, displays the loaded configuration file (excluding token).";
        this.guildOnly = false;
    }


    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("All about " + event.getSelfUser().getName() + "!", null, event.getSelfUser().getAvatarUrl());
        eb.setColor(SlapBot.COLOR);

        /*
        Description Builder
         */
        StringBuilder sb = new StringBuilder();
        //general description
        sb.append(String.format("'Ello gov'na! I am **%s** (v%s), a bot that is tailored to the Slap Gaming Community!\r\n",
                event.getSelfUser().getName(),
                VERSION));
        //credits
        sb.append(String.format("I was written in Java using [JDA](https://github.com/DV8FromTheWorld/JDA) (v%s) " +
                        "and [JDA-Utilities](https://github.com/JDA-Applications/JDA-Utilities) (v%s).\r\n",
                JDAInfo.VERSION,
                JDAUtilitiesInfo.VERSION));
        //source and project
        sb.append("You can find the [source code](https://github.com/SlapGaming/SlapBot) " +
                "and [project tracker](https://github.com/SlapGaming/SlapBot/projects/1) on Github. " +
                "Feel free to suggest new features or contribute to the code!\r\n");
        //help
        sb.append(String.format("Type `%s%s` to see my commands!\r\n", event.getClient().getTextualPrefix(), event.getClient().getHelpWord()));
        //features
        sb.append("\r\n\r\nSome of my features include: ```css\r\n" +
                ":video_game: Looking-to-game\r\n" +
                ":twisted_rightwards_arrows: Teams generator\r\n" +
                ":hammer: Pun-ishment\r\n" +
                ":notes: Soundboard (suggested/soon)\r\n" +
                "```");
        //settings/stats header
        sb.append("\r\n\r\n**Settings from config.yaml and some stats:**");
        eb.setDescription(EmojiParser.parseToUnicode(sb.toString()));

        /*
        Bot settings
        Make sure multiples of 3
         */
        eb.addField("Command Prefix", String.format("`%s` or `%s`", slapBot.getPrefix(), slapBot.getAltPrefix()), true);
        eb.addField("Guild", slapBot.getGuild().getName(), true);
        eb.addBlankField(true);

        eb.addField("Bot Owner", slapBot.getOwner().getName(), true);
        eb.addField("Bot Admins", slapBot.getAdminRole().getName(), true);
        eb.addField("Bot Moderators", slapBot.getModeratorRole().getName(), true);

        /*
        // THis does not allign nicely, so uncommented for now...
        eb.addField("LTG Channel", slapBot.getLtgTxChannel().getAsMention(), true);
        eb.addField("Pun Role", slapBot.getPunRole().getName(), true);
        eb.addField("Pun Channel", slapBot.getPunVcChannel().getName(), true);
        */


        /*
        Add some stats at the bottom
        Make sure multiples of 3
         */
        if (event.getJDA().getShardInfo() == null) {
            String stats = String.format("%s servers\n1 shard", event.getJDA().getGuilds().size());
            eb.addField("Stats", stats, true);

            String users = String.format("%s unique\n%s total",
                    event.getJDA().getUsers().size(),
                    event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers().size()).sum());
            eb.addField("Users", users, true);

            String channels = String.format("%s Text\n%s Voice",
                    event.getJDA().getTextChannels().size(),
                    event.getJDA().getVoiceChannels().size());
            eb.addField("Channels", channels, true);
        } else {
            String stats = String.format("%s Servers\nShard %d/%d",
                    event.getClient().getTotalGuilds(),
                    event.getJDA().getShardInfo().getShardId() + 1,
                    event.getJDA().getShardInfo().getShardTotal());
            eb.addField("Stats", stats, true);

            String users = String.format("%s Users\n%s Servers",
                    event.getJDA().getUsers().size(),
                    event.getJDA().getGuilds().size());
            eb.addField("This shard", users, true);

            String channels = String.format("%s Text Channels\n%s Voice Channels",
                    event.getJDA().getTextChannels().size(),
                    event.getJDA().getVoiceChannels().size());
            eb.addField("", channels, true);
        }

        /*
        Set footer
         */
        eb.setFooter("Last restart", null);
        eb.setTimestamp(event.getClient().getStartTime());

        /*
        Build and display
         */
        event.reply(eb.build());
    }
}
