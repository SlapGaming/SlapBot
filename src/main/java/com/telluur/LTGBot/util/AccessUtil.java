package com.telluur.LTGBot.util;

import com.telluur.LTGBot.LTGBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

/**
 * Some helpful functions for checking roles and permissions
 *
 * @author Rick Fontein
 */

public class AccessUtil {

    /**
     * Checks whether member is an admin
     *
     * @param ltgBot the bot instance containing the roles
     * @param member the member to be checked for access
     * @return whether access should be granted
     */
    public static boolean isAdmin(LTGBot ltgBot, Member member) {
        return hasRole(member, ltgBot.getAdminRole());
    }

    /**
     * Checks whether member is moderator or higher
     *
     * @param ltgBot the bot instance containing the roles
     * @param member the member to be checked for access
     * @return whether access should be granted
     */
    public static boolean isModerator(LTGBot ltgBot, Member member) {
        return isAdmin(ltgBot, member) || hasRole(member, ltgBot.getModeratorRole());
    }


    /**
     * Checks whether a member has a certain role
     *
     * @param member
     * @param role
     * @return whether the member has role
     */
    public static boolean hasRole(Member member, Role role) {
        return member.getRoles().contains(role);
    }
}
