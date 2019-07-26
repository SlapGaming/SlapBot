package com.telluur.SlapBot.util;

import com.telluur.SlapBot.SlapBot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some helpful functions for checking roles and permissions
 *
 * @author Rick Fontein
 */

public class AccessUtil {
    protected static final Logger logger = LoggerFactory.getLogger("ACCESSUTIL");

    public static boolean isOwner(SlapBot slapBot, User user) {
        return user.equals(slapBot.getOwner());
    }

    /**
     * Checks whether member is an admin
     *
     * @param slapBot the bot instance containing the roles
     * @param user   the user to be checked for access
     * @return whether access should be granted
     */
    public static boolean isAdmin(SlapBot slapBot, User user) {
        Guild guild = slapBot.getGuild();
        if (guild.isMember(user)) {
            return isOwner(slapBot, user) || hasRole(guild.getMember(user), slapBot.getAdminRole());
        } else {
            return false;
        }
    }

    /**
     * Checks whether member is moderator or higher
     *
     * @param slapBot the bot instance containing the roles
     * @param user   the user to be checked for access
     * @return whether access should be granted
     */
    public static boolean isModerator(SlapBot slapBot, User user) {
        Guild guild = slapBot.getGuild();
        if (guild.isMember(user)) {
            Member member = guild.getMember(user);
            return isOwner(slapBot, user) || hasRole(member, slapBot.getAdminRole()) || hasRole(member, slapBot.getModeratorRole());
        } else {
            return false;
        }
    }


    /**
     * Checks whether a member has a certain role.
     * Should only be called from this class, as it makes sure that member is never null.
     *
     * @param member
     * @param role
     * @return whether the member has role
     */
    private static boolean hasRole(Member member, Role role) {
        return member.getRoles().contains(role);
    }
}
