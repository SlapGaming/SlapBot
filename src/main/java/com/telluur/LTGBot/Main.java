package com.telluur.LTGBot;


import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.telluur.LTGBot.commands.admin.GetConfigCommand;
import com.telluur.LTGBot.commands.admin.KillCommand;
import com.telluur.LTGBot.commands.admin.ltg.ForceReloadCommand;
import com.telluur.LTGBot.commands.admin.ltg.ForceSaveCommand;
import com.telluur.LTGBot.commands.moderator.AddGameCommand;
import com.telluur.LTGBot.commands.moderator.RemoveGameCommand;
import com.telluur.LTGBot.commands.user.PingCmd;
import com.telluur.LTGBot.commands.user.ltg.GamesCommand;
import com.telluur.LTGBot.commands.user.ltg.SubscribeCommand;
import com.telluur.LTGBot.commands.user.ltg.SubscriptionsCommand;
import com.telluur.LTGBot.commands.user.ltg.UnsubscribeCommand;
import com.telluur.LTGBot.config.Config;
import com.telluur.LTGBot.config.ConfigLoader;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

/**
 * Entry point of the Looking-to-game bot
 *
 * @author Rick Fontein
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger("SYSTEM");

    public static void main(String[] args) {
        logger.info("Starting up");
        logger.info("Loading config.yaml");
        Config config = ConfigLoader.loadYAML();

        logger.info("Bot start");
        LTGBot ltgBot = new LTGBot(config);

        logger.info("Building commands");
        CommandClientBuilder cmdBuilder = new CommandClientBuilder();
        cmdBuilder.setOwnerId(config.getOwner());
        cmdBuilder.setPrefix(config.getPrefix());
        cmdBuilder.setAlternativePrefix(config.getAltprefix());
        cmdBuilder.setGame(config.getGameStatus());
        cmdBuilder.addCommands(
                /*
                Listen in alphabetical order
                Admin
                 */
                new ForceSaveCommand(ltgBot),
                new ForceReloadCommand(ltgBot),
                new GetConfigCommand(ltgBot),
                new KillCommand(ltgBot),

                /*
                Moderator
                 */
                new AddGameCommand(ltgBot),
                new RemoveGameCommand(ltgBot),

                /*
                User
                 */
                new PingCmd(ltgBot),
                new GamesCommand(ltgBot),
                new SubscriptionsCommand(ltgBot),
                new SubscribeCommand(ltgBot),
                new UnsubscribeCommand(ltgBot)
        );
        CommandClient cmdClient = cmdBuilder.build();

        logger.info("Building JDA client and logging in");
        try {
            String token = config.getToken();
            JDA jda = new JDABuilder()
                    .setToken(token)
                    .addEventListener(cmdClient)
                    .setAudioEnabled(false)
                    .setGame(Game.playing(EmojiParser.parseToUnicode("with myself...")))
                    .build();
            jda.awaitReady();
            ltgBot.finishBot(jda);
        } catch (LoginException e) {
            logger.error("Failed to login", e.getCause());
            shutdown("caught exception");
        } catch (IllegalArgumentException e) {
            logger.error("Malformed config file", e.getCause());
            shutdown("caught exception");
        } catch (InterruptedException e) {
            logger.error("Could not complete JDA", e.getCause());
            shutdown("caught exception");
        }
    }

    public static void shutdown(String reason) {
        logger.info(String.format("Shutting down (%s)", reason));
        System.exit(1);
    }
}
