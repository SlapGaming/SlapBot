package com.telluur.SlapBot.features.ltg.storage;

import lombok.*;

/**
 * POJO for the games stored in the storage.yaml file
 * <p>
 * private NoArgsConstructor for jackson databind
 *
 * @author Rick Fontein
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StoredGame {
    @Getter @Setter private String abbreviation, fullName;
}
