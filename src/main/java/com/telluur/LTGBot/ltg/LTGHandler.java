package com.telluur.LTGBot.ltg;

import com.telluur.LTGBot.LTGBot;
import com.telluur.LTGBot.Main;
import com.telluur.LTGBot.ltg.storage.StorageHandler;
import com.telluur.LTGBot.ltg.storage.StoredGame;
import lombok.Getter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Handles the creation/deletion and joining/leaving of the game roles.
 *
 * @author Rick Fontein
 */

public class LTGHandler {
    private static final Logger logger = LoggerFactory.getLogger("LTG");

    private LTGBot ltgBot;
    private Guild guild;
    private GuildController guildController;

    @Getter private StorageHandler storageHandler;


    public LTGHandler(LTGBot ltgBot) {
        try {
            this.ltgBot = ltgBot;
            this.guild = ltgBot.getGuild();
            this.guildController = ltgBot.getGuild().getController();
            this.storageHandler = new StorageHandler();
        } catch (IOException e) {
            logger.error("Failed to read storage", e.getCause());
            Main.shutdown("Caught Exception");
        }
    }

    /*
    JOIN/LEAVE/SUBSCRIBE/UNSUBSCRIBE
     */

    /**
     * Creates a new role in the discord guild
     *
     * @param name    The name for the new role
     * @param success Consumer that accepts a Role when adding was sucessful
     * @param failure Consumer that accepts an exception when creation failed
     */
    public void createGameRole(String name, Consumer<Role> success, Consumer<Throwable> failure) {
        guildController.createRole()
                .setName(name)
                .setPermissions(Permission.EMPTY_PERMISSIONS)
                .setMentionable(true)
                .setColor(new Color(26, 188, 156))
                .queue(
                        role -> {
                            try {

                                StoredGame storedGame = new StoredGame(name);
                                storageHandler.setGameBySnowflake(role.getId(), storedGame);
                                success.accept(role);
                                logger.info(String.format("Role `%s` with id `%s` created", role.getName(), role.getId()));
                            } catch (IOException e) {
                                role.delete().queue();
                                failure.accept(e.getCause());
                                logger.error(String.format("Failed to write role `%s` to storage", role.getName()));
                            }
                        },
                        fail -> {
                            logger.error(String.format("Failed to create Discord role `%s`", name));
                            failure.accept(fail);
                        });

    }

    /**
     * Attempts to delete a gamerole from the guild
     *
     * @param role    the role to be deleted
     * @param success success callback
     * @param failure failure callback
     */
    public void deleteGameRole(Role role, Consumer<Void> success, Consumer<Throwable> failure) {
        String snowflake = role.getId();
        if (storageHandler.hasGameBySnowflake(snowflake)) {
            role.delete().queue(
                    ok -> {
                        try {
                            storageHandler.deleteGameBySnowflake(snowflake);
                            logger.info("Successfully deleted role `%s` with id `%s`.");
                            success.accept(ok);
                        } catch (IOException e) {
                            failure.accept(e.getCause());
                        }
                    },
                    fail -> {
                        logger.error(String.format("Failed to delete Discord role `%s`", role.getName()));
                        failure.accept(fail);
                    });
        } else {
            failure.accept(new IllegalArgumentException(String.format("Role `%s` is not a LTG role", role.getName())));
        }
    }

    /**
     * Adds a role to a user when both the user and role are part of the guild
     *
     * @param role    the role to be added
     * @param user    the user to add the role to
     * @param success success callback
     * @param failure failure callback
     */
    public void joinGameRole(Role role, User user, Consumer<Void> success, Consumer<Throwable> failure) {
        if (!guild.isMember(user)) {
            failure.accept(new IllegalArgumentException(String.format("User `%s` is not a member of `%s`", user.getName(), guild.getName())));
        } else if (!storageHandler.hasGameBySnowflake(role.getId())) {
            failure.accept(new IllegalArgumentException(String.format("Role `%s` is not a LTG role", role.getName())));
        } else {
            Member member = guild.getMember(user);
            guildController.addRolesToMember(member, role).queue(
                    ok -> {
                        logger.info(String.format("User `%s` subscribed to `%s`", user.getName(), role.getName()));
                        success.accept(ok);
                    },
                    fail -> {
                        logger.error(String.format("Failed to add user `%s` to role `%s`", user.getName(), role.getName()));
                        failure.accept(fail);
                    });
        }
    }

    /**
     * Removes a role from a user when both the user and role are part of the guild.
     *
     * @param role    the role to be removed
     * @param user    the user from which the role is removed
     * @param success success callback
     * @param failure failure callback
     */
    public void leaveGameRole(Role role, User user, Consumer<Void> success, Consumer<Throwable> failure) {
        if (!guild.isMember(user)) {
            failure.accept(new IllegalArgumentException(String.format("User `%s` is not a member of `%s`", user.getName(), guild.getName())));
        } else if (!storageHandler.hasGameBySnowflake(role.getId())) {
            failure.accept(new IllegalArgumentException(String.format("Role `%s` is not a LTG role", role.getName())));
        } else {
            Member member = guild.getMember(user);
            guildController.removeRolesFromMember(member, role).queue(success, failure);
        }
    }
}
