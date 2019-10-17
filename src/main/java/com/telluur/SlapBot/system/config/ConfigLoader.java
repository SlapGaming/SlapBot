package com.telluur.SlapBot.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.telluur.SlapBot.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Loads the config file and returns the Config bean.
 *
 * @author Rick Fontein
 */


public class ConfigLoader {
    static final Logger logger = LoggerFactory.getLogger("SYSTEM");

    public static Config loadYAML() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Config config = mapper.readValue(new File("yaml/config.yaml"), Config.class);
            switch (config.getStatustype()) {
                case "watching":
                    break;
                case "listening":
                    break;
                case "playing":
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status type");
            }
            logger.info("================= CONFIG =================");
            logger.debug("Discord token:     " + config.getToken());
            logger.info("Guild:             " + config.getGuild());
            logger.info("Owner User:        " + config.getOwner());
            logger.info("Admin Role:        " + config.getAdmin());
            logger.info("Moderator Role:    " + config.getModerator());
            logger.info("Command prefix:    " + config.getPrefix());
            logger.info("Command altprefix: " + config.getAltprefix());
            logger.info("Status Type:       " + config.getStatustype());
            logger.info("Status:            " + config.getStatus());
            logger.info("Gen tx channel:    " + config.getGenTxChannel());
            logger.info("LTG tx channel:    " + config.getLtgTxChannel());
            logger.info("NSA tx channel:    " + config.getNsaTxChannel());
            logger.info("Pun channel:       " + config.getPunVcChannel());
            logger.info("Pun Role:          " + config.getPunRole());
            logger.info("==========================================");
            return config;
        } catch (Exception e) {
            logger.error("Failed to load config.yaml", e.getCause());
            Main.shutdown("caught exception");
        }
        //This statement should never be reached, as the bot should exit if the config couldn't be loaded.
        return null;
    }
}
