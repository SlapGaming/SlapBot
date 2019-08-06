package com.telluur.SlapBot.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Some util functions to meet embed constraints
 *
 * @author Rick Fontein
 */

public class EmbedUtil {

    /**
     * The discord limit for message embed discription is 2048.
     * We need to split the games to fit a message embed.
     *
     * @param source the list of games.
     * @return the concat list of reply messages under 2048 chars.
     */
    public static List<String> splitDiscordLimit(String[] source) {
        List<String> result = new LinkedList<>();
        StringBuilder part = new StringBuilder();
        for (String s : source) {
            if ((part.length() + s.length() + 2) >= 2000) {
                result.add(part.toString());
                part = new StringBuilder();

            }
            part.append(s).append("\r\n");
        }
        result.add(part.toString());

        return result;
    }
}
