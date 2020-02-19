package com.telluur.SlapBot.features.slapevents;

import com.telluur.SlapBot.SlapBot;
import org.joda.time.DateTime;

/**
 * Some help methods to juggle the DateTime string representation.
 *
 * @author Rick Fontein
 */

public class SlapEventUtil {

    public static DateTime toDateTimeOrNull(String str) {
        if (str == null) {
            return null;
        } else {
            try {
                return new DateTime(str).withZone(SlapBot.TIME_ZONE);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static String toISOStringOrNull(DateTime dt) {
        if (dt == null) {
            return null;
        } else {
            return dt.toDateTimeISO().toString();
        }
    }
}
