package com.telluur.SlapBot;


import com.telluur.SlapBot.system.config.Config;
import com.telluur.SlapBot.system.config.ConfigLoader;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Objects;

/**
 * Entry point of the Looking-to-game bot
 *
 * @author Rick Fontein
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger("SYSTEM");

    public static void main(String[] args) {
        logger.info("System startup");
        final String VERSION = Main.class.getPackage().getImplementationVersion() != null ?
                Main.class.getPackage().getImplementationVersion() :
                "DEVELOPMENT NON PACKAGED";
        logger.info(String.format("Starting bot, version: [%s]", VERSION));
        logger.info("Loading config.yaml");
        Config config = ConfigLoader.loadYAML();

        try {
            logger.info("Building JDA client and logging in");
            String token = Objects.requireNonNull(config).getToken();
            JDA jda = new JDABuilder()
                    .setToken(token)
                    .setActivity(Activity.playing(EmojiParser.parseToUnicode("with myself...")))
                    .build();
            jda.awaitReady();

            new SlapBot(jda, config);
        } catch (LoginException | IllegalArgumentException | InterruptedException | IOException e) {
            logger.error(e.getMessage());
            shutdown("caught exception");
        }
    }

    public static void shutdown(String reason) {
        logger.info(String.format("Shutting down (%s)", reason));
        System.exit(1);
    }
}
