package com.telluur.SlapBot.features.avatar;

import com.telluur.SlapBot.SlapBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Unifies the update listener and command embed for the avatar update.
 *
 * @author Rick Fontein
 */

public class AvatarMessageBuilder {

    public static MessageEmbed buildEmbed(String title, String url) {
        return new EmbedBuilder()
                .setColor(SlapBot.COLOR)
                .setTitle(title)
                .setImage(String.format("%s%s", url, "?size=2048"))
                .build();
    }
}
