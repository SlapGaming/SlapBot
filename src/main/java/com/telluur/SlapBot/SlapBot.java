package com.telluur.SlapBot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.telluur.SlapBot.commands.AboutCommand;
import com.telluur.SlapBot.commands.PingCommand;
import com.telluur.SlapBot.commands.VersionCommand;
import com.telluur.SlapBot.commands.admin.EvalCommand;
import com.telluur.SlapBot.commands.admin.EventManageCommand;
import com.telluur.SlapBot.commands.admin.KillCommand;
import com.telluur.SlapBot.commands.admin.PruneChatCommand;
import com.telluur.SlapBot.commands.moderator.AddGameCommand;
import com.telluur.SlapBot.commands.moderator.RemoveGameCommand;
import com.telluur.SlapBot.commands.user.*;
import com.telluur.SlapBot.commands.user.ltg.GamesCommand;
import com.telluur.SlapBot.commands.user.ltg.SubscribeCommand;
import com.telluur.SlapBot.commands.user.ltg.SubscriptionsCommand;
import com.telluur.SlapBot.commands.user.ltg.UnsubscribeCommand;
import com.telluur.SlapBot.features.avatar.AvatarUpdateListener;
import com.telluur.SlapBot.features.joinnotifier.JoinNotifierListener;
import com.telluur.SlapBot.features.ltg.LTGHandler;
import com.telluur.SlapBot.features.ltg.listeners.LTGChatListener;
import com.telluur.SlapBot.features.ltg.listeners.QuickSubscribeListener;
import com.telluur.SlapBot.features.nsa.NSAChatListener;
import com.telluur.SlapBot.features.slapevents.jpa.SlapEventRepository;
import com.telluur.SlapBot.system.config.Config;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.awt.*;
import java.io.IOException;

/**
 * Singleton JDA bot class
 *
 * @author Rick Fontein
 */

public class SlapBot {
    /*
    In bot constants & Config
    TODO: Clean this up, move stuff in here away from yaml storage.
     */

    public static final String VERSION = Main.class.getPackage().getImplementationVersion() != null ?
            Main.class.getPackage().getImplementationVersion() : "DEV";
    public static final DateTimeZone TIME_ZONE = DateTimeZone.forID("Europe/London");
    public static final Color COLOR = Color.ORANGE;
    private static final Logger logger = LoggerFactory.getLogger("SYSTEM");
    private final Config config;
    @Getter
    private final String prefix, altPrefix; //String

    /*
    Hibernate entity manager
     */
    @Getter
    private final EntityManager entityManager;

    /*
    JDA
     */
    @Getter
    private final JDA jda;

    /*
    Commands
     */
    @Getter
    private final CommandClient commandClient;

    /*
    Eventwaiter
     */
    @Getter
    private final EventWaiter eventWaiter;

    /*
    Looking to game
     */
    @Getter
    private final LTGHandler ltgHandler;
    @Getter
    private final LTGChatListener ltgChatListener;
    @Getter
    private final QuickSubscribeListener quickSubscribeListener;

    /*
    Avatar updates
     */
    @Getter
    private final AvatarUpdateListener avatarUpdateListener;

    /*
    Join roles
     */
    @Getter
    private final JoinNotifierListener joinNotifierListener;

    /*
    Slap Events
     */
    @Getter
    private final SlapEventRepository slapEventRepository;

    /*
    NSA
     */
    @Getter
    private final NSAChatListener nsaChatListener;


    public SlapBot(JDA jda, Config config) throws IOException {
        logger.info("Building SlapBot");
        /*
        Constructor Args
         */
        this.jda = jda;
        this.config = config;

        /*
        Config file settings
         */
        this.prefix = config.getPrefix();
        this.altPrefix = config.getAltprefix();

        /*
        Validate config IDs
         */
        logger.info("Validating discord IDs in config");
        //TODO Validate config IDs

        /*
        Connecting to storage backend
         */
        logger.info("Setting up hibernate session");
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("slapbot");
        this.entityManager = entityManagerFactory.createEntityManager();

        /*
        Eventwaiter for JDA utilities
         */
        logger.info("Building JDA-Utilities Event Waiter");
        this.eventWaiter = new EventWaiter();
        jda.addEventListener(this.eventWaiter);

        /*
        Looking to game
         */
        logger.info("Building Looking-To-Game");
        this.ltgHandler = new LTGHandler(this);
        this.ltgChatListener = new LTGChatListener(this);
        this.quickSubscribeListener = new QuickSubscribeListener(this);
        jda.addEventListener(this.ltgChatListener, this.quickSubscribeListener);

        /*
        Avatar update listener
         */
        logger.info("Building Avatar Updater");
        this.avatarUpdateListener = new AvatarUpdateListener(this);
        jda.addEventListener(this.avatarUpdateListener);

        /*
        Join roles
         */
        logger.info("Building Join Roles Assigner");
        this.joinNotifierListener = new JoinNotifierListener(this);
        jda.addEventListener(this.joinNotifierListener);

        /*
        Slap Events
         */
        logger.info("Building Slap Events");
        this.slapEventRepository = new SlapEventRepository(this);

        /*
        NSA
         */
        logger.info("Building NSA");
        this.nsaChatListener = new NSAChatListener(this);
        jda.addEventListener(this.nsaChatListener);

        /*
        Commands
         */
        logger.info("Building Command Client");
        this.commandClient = new CommandClientBuilder()
                .setOwnerId(config.getOwner())
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltprefix())
                .setActivity(config.getGameStatus())
                .addCommands(
                    /*
                    Listen in alphabetical order
                    About command
                     */
                        new AboutCommand(this),
                        new VersionCommand(this),
                        new PingCommand(this),

                    /*
                    Admin
                    */
                        new EvalCommand(this),
                        new KillCommand(this),
                        new EventManageCommand(this),
                        new PruneChatCommand(this),

                    /*
                    Moderator
                    */
                        new AddGameCommand(this),
                        new RemoveGameCommand(this),

                    /*
                    User
                     */
                        new AvatarCommand(this),
                        new EventCommand(this),
                        new PunCommand(this),
                        new RollCommand(this),
                        new TeamsCommand(this),
                        new GamesCommand(this, eventWaiter),
                        new SubscriptionsCommand(this, eventWaiter), //info
                        new SubscribeCommand(this), //join
                        new UnsubscribeCommand(this) //leave
                ).build();
        jda.addEventListener(this.commandClient);

        logger.info("SlapBot Build Complete. Ready.");
    }

    public Guild getGuild() {
        return jda.getGuildById(config.getGuild());
    }

    public User getOwner() {
        return jda.getUserById(config.getOwner());
    }

    public Role getAdminRole() {
        return jda.getRoleById(config.getAdmin());
    }

    public Role getModeratorRole() {
        return jda.getRoleById(config.getModerator());
    }

    public Role getPunRole() {
        return jda.getRoleById(config.getPunRole());
    }

    public TextChannel getGenTxChannel() {
        return jda.getTextChannelById(config.getGenTxChannel());
    }

    public TextChannel getLtgTxChannel() {
        return jda.getTextChannelById(config.getLtgTxChannel());
    }

    public TextChannel getNsaTxChannel() {
        return jda.getTextChannelById(config.getNsaTxChannel());
    }

    public VoiceChannel getPunVcChannel() {
        return jda.getVoiceChannelById(config.getPunVcChannel());
    }
}
