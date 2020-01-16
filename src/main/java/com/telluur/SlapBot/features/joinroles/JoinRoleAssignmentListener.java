package com.telluur.SlapBot.features.joinroles;

import com.telluur.SlapBot.SlapBot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Adds a role to a new member when a specific join link has been used.
 *
 * @author Rick Fontein
 */

public class JoinRoleAssignmentListener implements EventListener {
    private static final Logger ltgLogger = LoggerFactory.getLogger("LTG");
    private final SlapBot bot;
    private InviteTracker wow = new InviteTracker("aZu2FEP", "604512744640872449", 0);

    public JoinRoleAssignmentListener(SlapBot bot) {
        this.bot = bot;
    }

    @Override
    public void onEvent(@Nonnull GenericEvent genericEvent) {
        if (genericEvent instanceof GuildMemberJoinEvent) {
            GuildMemberJoinEvent event = (GuildMemberJoinEvent) genericEvent;
            bot.getGuild().retrieveInvites().queue(result -> {

                int wowUses = findInviteCountByCode(result, wow.getCode());

                if (wowUses > wow.getInvitationCount()) {
                    wow.setInvitationCount(wowUses);
                    assignRoleAndNotify(event.getMember(), wow);
                } else {
                    bot.getGenTxChannel().sendMessage(
                            String.format("Welcome `%s`! No roles assigned.", event.getMember())
                    ).queue();
                }
            });
        }
    }

    /**
     * Updates the invitation counts for the defined InviteTrackers
     */
    public void inviteCountUpdate() {
        bot.getGuild().retrieveInvites().queue(
                result -> wow.setInvitationCount(findInviteCountByCode(result, wow.getCode()))
        );
    }

    /**
     * Find the number of uses for a code, throws when illegal code is supplied
     *
     * @param invites List of Invites (discord API)
     * @param code    The invite Code
     * @return number of uses for code
     * @throws IllegalArgumentException when code isn't found.
     */
    private int findInviteCountByCode(List<Invite> invites, String code) throws IllegalArgumentException {
        for (Invite inv : invites) {
            if (inv.getCode().equals(code)) {
                return inv.getUses();
            }
        }
        throw new IllegalArgumentException("Could not find code " + code);
    }


    private void assignRoleAndNotify(Member member, InviteTracker inviteTracker) {
        Guild guild = bot.getGuild();

        // Add role
        Role role = guild.getRoleById(inviteTracker.getRoleID());
        if (role != null) {
            guild.addRoleToMember(member, role).queue(
                    ok -> {
                        String msg = String.format("Assigned `%s` to `%s`, welcome!", role.getName(), member.getEffectiveName());
                        bot.getGenTxChannel().sendMessage(msg).queue();
                        ltgLogger.info(String.format("User `%s` subscribed to `%s`", member.getEffectiveName(), role.getName()));
                    }
            );
        }
    }

    /**
     * Custom wrapper for the invite links.
     * code: Discord invite code used
     * roleId: The role to be assigned
     * invitationCount: number of uses of `code` as retrieved by discord api.
     */
    @AllArgsConstructor
    private class InviteTracker {
        @Getter private String code;
        @Getter private String roleID;
        @Getter @Setter private int invitationCount;
    }
}
