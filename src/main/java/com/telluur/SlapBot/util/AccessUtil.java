package com.telluur.SlapBot.util;

import com.telluur.SlapBot.SlapBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.Comparator;
import java.util.Optional;

/**
 * Some helpful functions for checking roles and permissions
 *
 * @author Rick Fontein
 */

@SuppressWarnings("WeakerAccess")
public class AccessUtil {

    public static boolean isOwner(SlapBot slapBot, User user) {
        return user.equals(slapBot.getOwner());
    }

    /**
     * Checks whether member is an admin
     *
     * @param slapBot the bot instance containing the roles
     * @param user    the user to be checked for access
     * @return whether access should be granted
     */
    public static boolean isAdmin(SlapBot slapBot, User user) {
        Guild guild = slapBot.getGuild();
        Member member = guild.getMember(user);
        if (member != null) {
            return isGuildAdmin(member) || isOwner(slapBot, user) || hasRole(member, slapBot.getAdminRole());
        } else {
            return false;
        }
    }

    /**
     * Checks whether member is moderator or higher
     *
     * @param slapBot the bot instance containing the roles
     * @param user    the user to be checked for access
     * @return whether access should be granted
     */
    public static boolean isModerator(SlapBot slapBot, User user) {
        Guild guild = slapBot.getGuild();
        Member member = guild.getMember(user);
        if (member != null) {
            return isOwner(slapBot, user) || hasRole(member, slapBot.getAdminRole()) || hasRole(member, slapBot.getModeratorRole());
        } else {
            return false;
        }
    }


    /**
     * Checks whether a member has a certain role.
     * Should only be called from this class, as it makes sure that member is never null.
     *
     * @param member involved member
     * @param role   involved role
     * @return whether the member has role
     */
    private static boolean hasRole(Member member, Role role) {
        return member.getRoles().contains(role);
    }

    /**
     * Returns the highest position of the a member
     *
     * @param member involved member
     * @return int highest role position
     */
    public static int getHighestRolePosition(Member member) {
        Optional<Integer> i = member.getRoles().stream()
                .map(Role::getPosition)
                .max(Comparator.naturalOrder());
        return i.orElse(0);
    }

    /**
     * Checks whether the user has a higher rank than the bot
     *
     * @param member involved member
     * @return whether the user has a higher rank than the bot
     */
    public static boolean hasHigherRoleThanBot(Member member) {
        int memberPosition = getHighestRolePosition(member);
        int botPosition = getHighestRolePosition(member.getGuild().getSelfMember());
        return memberPosition > botPosition;
    }

    /**
     * Whether the member is a guildAdmin
     *
     * @param member involved member
     * @return Whether the member is a guildAdmin
     */
    public static boolean isGuildAdmin(Member member) {
        return member.hasPermission(Permission.MANAGE_SERVER);
    }
}
