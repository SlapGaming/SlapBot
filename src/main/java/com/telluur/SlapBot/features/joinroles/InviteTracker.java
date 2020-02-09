package com.telluur.SlapBot.features.joinroles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Custom wrapper for the invite links.
 * code: Discord invite code used
 * roleId: The role to be assigned
 * invitationCount: number of uses of `code` as retrieved by discord api.
 */
@AllArgsConstructor
class InviteTracker {
    @Getter private String code;
    @Getter @Setter private int localInvitationCount;
    @Getter private String roleID;
    @Getter private String textChannelID;
}

