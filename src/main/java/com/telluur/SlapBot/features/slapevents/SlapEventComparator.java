package com.telluur.SlapBot.features.slapevents;

import java.util.Comparator;

/**
 * Comparator for SlapEvents that orders by their start date.
 *
 * @author Rick Fontein
 */

public class SlapEventComparator implements Comparator<OldSlapEvent> {
    @Override
    public int compare(OldSlapEvent o1, OldSlapEvent o2) {
        return o1.getStart().compareTo(o2.getStart());
    }
}
