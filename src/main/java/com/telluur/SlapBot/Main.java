package com.telluur.SlapBot;


import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.telluur.SlapBot.commands.AboutCommand;
import com.telluur.SlapBot.commands.PingCmd;
import com.telluur.SlapBot.commands.admin.EvalCommand;
import com.telluur.SlapBot.commands.admin.KillCommand;
import com.telluur.SlapBot.commands.admin.PruneChatCommand;
import com.telluur.SlapBot.commands.admin.ltg.ForceReloadCommand;
import com.telluur.SlapBot.commands.admin.ltg.ForceSaveCommand;
import com.telluur.SlapBot.commands.moderator.AddGameCommand;
import com.telluur.SlapBot.commands.moderator.RemoveGameCommand;
import com.telluur.SlapBot.commands.user.AvatarCommand;
import com.telluur.SlapBot.commands.user.PunCommand;
import com.telluur.SlapBot.commands.user.TeamsCommand;
import com.telluur.SlapBot.commands.user.ltg.GamesCommand;
import com.telluur.SlapBot.commands.user.ltg.SubscribeCommand;
import com.telluur.SlapBot.commands.user.ltg.SubscriptionsCommand;
import com.telluur.SlapBot.commands.user.ltg.UnsubscribeCommand;
import com.telluur.SlapBot.features.avatar.AvatarUpdateListener;
import com.telluur.SlapBot.features.ltg.LTGChatListener;
import com.telluur.SlapBot.system.config.Config;
import com.telluur.SlapBot.system.config.ConfigLoader;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Objects;

/**
 * Entry point of the Looking-to-game bot
 *
 * @author Rick Fontein
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger("SYSTEM");

    public static void main(String[] args) {
        final String VERSION = Main.class.getPackage().getImplementationVersion() != null ?
                Main.class.getPackage().getImplementationVersion() :
                "DEVELOPMENT NON PACKAGED";
        logger.info(String.format("Starting bot, version: [%s]", VERSION));
        logger.info("Loading config.yaml");
        Config config = ConfigLoader.loadYAML();

        logger.info("Bot start");
        EventWaiter waiter = new EventWaiter();
        SlapBot slapBot = new SlapBot(Objects.requireNonNull(config), waiter);
        LTGChatListener ltgChatListener = new LTGChatListener(slapBot);
        AvatarUpdateListener avatarUpdateListener = new AvatarUpdateListener(slapBot);

        logger.info("Building commands");
        CommandClientBuilder cmdBuilder = new CommandClientBuilder();
        cmdBuilder.setOwnerId(config.getOwner());
        cmdBuilder.setPrefix(config.getPrefix());
        cmdBuilder.setAlternativePrefix(config.getAltprefix());
        cmdBuilder.setActivity(config.getGameStatus());
        cmdBuilder.addCommands(
                /*
                Listen in alphabetical order
                About command
                 */
                new AboutCommand(slapBot),
                new PingCmd(),

                /*
                Admin
                 */
                new EvalCommand(slapBot),
                new ForceSaveCommand(slapBot),
                new ForceReloadCommand(slapBot),
                new KillCommand(slapBot),
                new PruneChatCommand(slapBot),

                /*
                Moderator
                 */
                new AddGameCommand(slapBot),
                new RemoveGameCommand(slapBot),

                /*
                User
                 */
                new AvatarCommand(slapBot),
                new PunCommand(slapBot),
                new TeamsCommand(slapBot),
                new GamesCommand(slapBot),
                new SubscriptionsCommand(slapBot), //info
                new SubscribeCommand(slapBot), //join
                new UnsubscribeCommand(slapBot) //leave
        );
        CommandClient cmdClient = cmdBuilder.build();

        logger.info("Building JDA client and logging in");
        try {
            String token = config.getToken();
            JDA jda = new JDABuilder()
                    .setToken(token)
                    .addEventListeners(cmdClient, waiter, ltgChatListener, avatarUpdateListener)
                    .setActivity(Activity.playing(EmojiParser.parseToUnicode("with myself...")))
                    .build();
            jda.awaitReady();
            slapBot.finishBot(jda);
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
