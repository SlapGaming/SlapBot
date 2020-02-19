package com.telluur.SlapBot.features.ltg;

import com.telluur.SlapBot.Main;
import com.telluur.SlapBot.SlapBot;
import com.telluur.SlapBot.features.ltg.storage.LTGStorageHandler;
import com.telluur.SlapBot.features.ltg.storage.StoredGame;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Handles the creation/deletion and joining/leaving of the game roles.
 *
 * @author Rick Fontein
 */

public class LTGHandler {
    private static final Logger logger = LoggerFactory.getLogger("LTG");
    @Getter private static final Color COLOR = new Color(17, 128, 106);
    private static final String communityRoleId = "663755925248802826";
    private Guild guild;
    @Getter private LTGStorageHandler storageHandler;


    public LTGHandler(SlapBot slapBot) {
        try {
            this.guild = slapBot.getGuild();
            this.storageHandler = new LTGStorageHandler();
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
     * @param abbreviation The game's abbreviation
     * @param fullname     The full game name
     * @param success      Consumer that accepts a Role when adding was sucessful
     * @param failure      Consumer that accepts an exception when creation failed
     */
    public void createGameRole(String abbreviation, String fullname, Consumer<Role> success, Consumer<Throwable> failure) {
        guild.createRole()
                .setName(String.format("%s | %s", abbreviation, fullname))
                .setPermissions(Permission.EMPTY_PERMISSIONS)
                .setMentionable(true)
                .setColor(COLOR)
                .queue(
                        role -> {
                            try {

                                StoredGame storedGame = new StoredGame(abbreviation, fullname);
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
                            logger.error(String.format("Failed to create Discord role `%s | %s`", abbreviation, fullname));
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
     * Adds the user to the community game role if they aren't already
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
            //Join role
            Member member = guild.getMember(user);
            guild.addRoleToMember(Objects.requireNonNull(member), role).queue(
                    ok -> {
                        logger.info(String.format("User `%s` subscribed to `%s`", user.getName(), role.getName()));
                        success.accept(ok);
                    },
                    fail -> {
                        logger.error(String.format("Failed to add user `%s` to role `%s`", user.getName(), role.getName()));
                        failure.accept(fail);
                    });

            //Add community role
            Role communityRole = guild.getRoleById(communityRoleId);
            if (communityRole != null && !member.getRoles().contains(communityRole)) {
                guild.addRoleToMember(member, communityRole).queue(
                        ok -> logger.info(String.format("User `%s` subscribed to `%s`", user.getName(), role.getName()))
                );
            }
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
            guild.removeRoleFromMember(Objects.requireNonNull(member), role).queue(success, failure);
        }
    }

    /**
     * Whether a role is a stored LTG role
     *
     * @param role the role to compare
     * @return whether role is a LTG role
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isGameRole(Role role) {
        return storageHandler.hasGameBySnowflake(role.getId());
    }
}
