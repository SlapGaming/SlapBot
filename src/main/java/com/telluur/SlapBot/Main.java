package com.telluur.SlapBot;


import com.telluur.SlapBot.system.config.Config;
import com.telluur.SlapBot.system.config.ConfigLoader;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Entry point of the Looking-to-game bot
 *
 * @author Rick Fontein
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger("SYSTEM");

    public static void main(String[] args) throws InterruptedException {
        logger.info("System startup");
        logger.info("Starting bot, version: [{}]", SlapBot.VERSION);
        logger.info("Loading config.yaml");
        Config config = ConfigLoader.loadYAML();

        try {
            logger.info("Building JDA client and logging in");
            String token = Objects.requireNonNull(config).getToken();
            JDA jda = JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class))
                    .setActivity(Activity.playing(EmojiParser.parseToUnicode("with myself...")))
                    .build();
            jda.awaitReady();

            new SlapBot(jda, config);
        } catch (LoginException | IllegalArgumentException | InterruptedException | IOException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            shutdown("caught exception");
        }
    }

    public static void shutdown(String reason) {
        logger.info("Shutting down ({})", reason);
        System.exit(1);
    }
}
