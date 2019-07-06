package com.telluur.LTGBot.util;

import com.telluur.LTGBot.LTGBot;
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

    public static boolean isOwner(LTGBot ltgBot, User user) {
        return user.equals(ltgBot.getOwner());
    }

    /**
     * Checks whether member is an admin
     *
     * @param ltgBot the bot instance containing the roles
     * @param user   the user to be checked for access
     * @return whether access should be granted
     */
    public static boolean isAdmin(LTGBot ltgBot, User user) {
        Guild guild = ltgBot.getGuild();
        if (guild.isMember(user)) {
            return isOwner(ltgBot, user) || hasRole(guild.getMember(user), ltgBot.getAdminRole());
        } else {
            return false;
        }
    }

    /**
     * Checks whether member is moderator or higher
     *
     * @param ltgBot the bot instance containing the roles
     * @param user   the user to be checked for access
     * @return whether access should be granted
     */
    public static boolean isModerator(LTGBot ltgBot, User user) {
        Guild guild = ltgBot.getGuild();
        if (guild.isMember(user)) {
            Member member = guild.getMember(user);
            return isOwner(ltgBot, user) || hasRole(member, ltgBot.getAdminRole()) || hasRole(member, ltgBot.getModeratorRole());
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
