package com.telluur.SlapBot.features.slapevents;

import java.util.Comparator;

/**
 * Comparator for SlapEvents that orders by their start date.
 *
 * @author Rick Fontein
 */

public class SlapEventComparator implements Comparator<SlapEvent> {
    @Override
    public int compare(SlapEvent o1, SlapEvent o2) {
        return o1.getStart().compareTo(o2.getStart());
    }
}
