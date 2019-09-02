package com.telluur.SlapBot.features.pun;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.entities.Member;

/**
 * Punishment object that keeps track of current punishments.
 *
 * @author Rick Fontein
 */

@AllArgsConstructor
@Data
public class Punishment {
    private Member punMember;
    private int timeout;
}
