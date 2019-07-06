package com.telluur.LTGBot.ltg;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Role;

/**
 * Model that describes a looking to play game
 *
 * @author Rick Fontein
 */
@RequiredArgsConstructor
public class LTPGame {
    /*
     * Implements a simple bean that holds the game role info.
     *
     * Use this a bean directly holding multiple games?
     * Storage simple YAML or more robust and ACID sql(ite)?
     */

    @NonNull @Getter private Role role;
    @NonNull @Getter private String identifier;
    @Getter @Setter private String fullName, url;


}
