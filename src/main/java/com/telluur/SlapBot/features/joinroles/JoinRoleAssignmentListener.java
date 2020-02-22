package com.telluur.SlapBot.features.joinroles;

import com.telluur.SlapBot.SlapBot;
import net.dv8tion.jda.api.entities.*;
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
    private InviteTracker wow = new InviteTracker("aZu2FEP", 0, "604512744640872449", "663438874113998859");

    public JoinRoleAssignmentListener(SlapBot bot) {
        this.bot = bot;
        inviteCountUpdate();
    }

    @Override
    public void onEvent(@Nonnull GenericEvent genericEvent) {
        if (genericEvent instanceof GuildMemberJoinEvent) {
            GuildMemberJoinEvent event = (GuildMemberJoinEvent) genericEvent;
            Guild guild = bot.getGuild();
            Member member = event.getMember();

            guild.retrieveInvites().queue(retrievedInvites -> {

                int wowUses = findInviteCountByCode(retrievedInvites, wow.getCode());

                if (wowUses == (wow.getLocalInvitationCount() + 1)) {
                    /* WOW Code used
                    - Add wow role to user
                    - Send message to wow general
                    - (Update wow invitation count, happens on every join event)
                     */
                    Role role = guild.getRoleById(wow.getRoleID());
                    TextChannel tx = guild.getTextChannelById(wow.getTextChannelID());
                    if (role != null && tx != null) {
                        guild.addRoleToMember(member, role).queue(
                                ok -> {
                                    tx.sendMessage(
                                            String.format(JoinMessages.randomJoinMessage(), member.getAsMention())
                                    ).queue();
                                    ltgLogger.info(String.format("User `%s` subscribed to `%s`", member.getEffectiveName(), role.getName()));
                                }
                        );
                    }
                } else {
                    // No tracked code used, send to general text.
                    bot.getGenTxChannel().sendMessage(
                            String.format(JoinMessages.randomJoinMessage(), member.getAsMention())
                    ).queue();
                }
                //Finally, update invite count.
                inviteCountUpdate(retrievedInvites);
            });
        }
    }

    /**
     * Updates the invitation counts for the defined InviteTrackers
     */
    public void inviteCountUpdate() {
        bot.getGuild().retrieveInvites().queue(
                result -> wow.setLocalInvitationCount(findInviteCountByCode(result, wow.getCode()))
        );
    }

    /**
     * Updates the invitation counts for the defined InviteTrackers by a supplied invites list
     * DOES NOT CHECK VALIDITY OF INVITES LIST.
     */
    private void inviteCountUpdate(List<Invite> invitesList) {
        wow.setLocalInvitationCount(findInviteCountByCode(invitesList, wow.getCode()));
    }

    /**
     * Find the number of uses for a code, throws when illegal code is8 supplied
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
}
