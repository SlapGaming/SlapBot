package com.telluur.SlapBot.features.slapevents;

import lombok.*;
import org.joda.time.DateTime;

/**
 * POJO for the events stored in the events.yaml file
 * <p>
 * private NoArgsConstructor for jackson databind
 *
 * @author Rick Fontein
 */

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SlapEvent {
    @Getter @Setter private String description, start, end = null;
}
