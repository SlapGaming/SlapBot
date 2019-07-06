package com.telluur.LTGBot.ltg;

import com.telluur.LTGBot.LTGBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Handles the creation/deletion and joining/leaving of the game roles.
 *
 * @author Rick Fontein
 */

public class LTPHandler {
    private static final String ROLE_PREFIX = "game:";
    private LTGBot ltgBot;
    private Guild guild;
    private GuildController guildController;

    public LTPHandler(LTGBot ltgBot) {
        this.ltgBot = ltgBot;
        this.guild = ltgBot.getGuild();
        this.guildController = ltgBot.getGuild().getController();
    }

    /*
    JOIN/LEAVE/SUBSCRIBE/UNSUBSCRIBE
     */

    /**
     * Creates a new role in the discord guild
     *
     * @param identifier The identifier for the new role
     * @param success    Consumer that accepts a Role when adding was sucessful
     * @param failure    Consumer that accepts an exception when creation failed
     */
    public void createGameRole(String identifier, Consumer<Role> success, Consumer<Throwable> failure) {
        if (identifier.contains(ROLE_PREFIX)) {
            failure.accept(new IllegalArgumentException("Identifier contains prefix"));
        } else if (getGameRoleidentifiers().contains(identifier)) {
            failure.accept(new IllegalArgumentException("Identifier already in use"));
        } else {
            String name = String.format("%s%s", ROLE_PREFIX, identifier);
            guildController.createRole()
                    .setName(name)
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .setMentionable(true)
                    .setColor(new Color(26, 188, 156))
                    .queue(success, failure);
        }
    }

    /**
     * Attempts to delete a gamerole from the guild
     *
     * @param role    the role to be deleted
     * @param success success callback
     * @param failure failure callback
     */
    public void deleteGameRole(Role role, Consumer<Void> success, Consumer<Throwable> failure) {
        if (!role.getName().contains(ROLE_PREFIX)) {
            failure.accept(new IllegalArgumentException("Not a LTP role"));
        } else {
            role.delete().queue(success, failure);
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
            failure.accept(new IllegalArgumentException("User is not a member of the guild"));
        } else if (!isGameRole(role)) {
            failure.accept(new IllegalArgumentException("Role is not a game role of the guild"));
        } else {
            Member member = guild.getMember(user);
            guildController.addRolesToMember(member, role).queue(success, failure);
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
            failure.accept(new IllegalArgumentException("User is not a member of the guild"));
        } else if (!isGameRole(role)) {
            failure.accept(new IllegalArgumentException("Role is not a game role of the guild"));
        } else {
            Member member = guild.getMember(user);
            guildController.removeRolesFromMember(member, role).queue(success, failure);
        }
    }

    /*
    UTILITY
     */

    /**
     * The list of roles on the guild that contain the gamerole prefix
     *
     * @return list of gameRoles
     */
    public List<Role> getGameRoles() {
        List<Role> r = ltgBot.getGuild()
                .getRoles()
                .stream()
                .filter(role -> role.getName().startsWith(ROLE_PREFIX))
                .collect(Collectors.toList());
        return r;
    }

    /**
     * Returns a list of game identifier strings
     */
    public List<String> getGameRoleidentifiers() {
        return getGameRoles()
                .stream()
                .map(role -> role.getName().replace(ROLE_PREFIX, ""))
                .collect(Collectors.toList());
    }

    /**
     * Checks whether the supplied role is a gamerole
     *
     * @param role the role to be checked
     * @return whether role is gamerole
     */
    public boolean isGameRole(Role role) {
        return getGameRoles().contains(role);
    }
}
