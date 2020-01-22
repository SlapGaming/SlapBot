package com.telluur.SlapBot.features.ltg;

import com.telluur.SlapBot.SlapBot;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Transforms LTG game role mentions into fancy embeds.
 *
 * @author Rick Fontein
 */

public class LTGChatListener implements EventListener {
    private static final String SUBSCRIBE = EmojiParser.parseToUnicode(":video_game:");
    private final SlapBot bot;
    private HashMap<String, Role> embeds = new HashMap<>();


    public LTGChatListener(SlapBot bot) {
        this.bot = bot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        //Chat listener
        if (event instanceof MessageReceivedEvent) {
            Message message = ((MessageReceivedEvent) event).getMessage();

            //Skip messages from the bot
            if (message.getAuthor().isBot()) {
                return;
            }

            //Skip command, as we're not interested in commands.
            if (message.getContentRaw().startsWith(bot.getPrefix()) || message.getContentRaw().startsWith(bot.getAltPrefix())) {
                return;
            }

            //Only listen to messages in guild text channels
            if (!message.isFromType(ChannelType.TEXT)) {
                return;
            }

            //Filter out the WOW section
            if (Objects.requireNonNull(bot.getGuild().getCategoryById("663438826890330133")).getTextChannels().contains(message.getTextChannel())){
                return;
            }

            //Message is not in the guild we're interested in.
            TextChannel textChannel = message.getTextChannel();
            List<TextChannel> guildChannels = bot.getGuild().getTextChannels();
            if (!guildChannels.contains(textChannel)) {
                return;
            }

            //Check whether the message contains a role mention and if that role is a LTG role
            List<Role> foundRoles = message.getMentionedRoles();
            if (foundRoles.size() == 1 && bot.getLtgHandler().isGameRole(foundRoles.get(0))) {
                //Delete the original message and create text embed
                Role role = foundRoles.get(0);

                MessageEmbed me = new EmbedBuilder()
                        .setColor(LTGHandler.getCOLOR())
                        .setTitle("Looking-to-game notifier")
                        .setDescription(String.format("> %s: %s",
                                Objects.requireNonNull(bot.getGuild().getMember(message.getAuthor())).getAsMention(),
                                message.getContentRaw()))
                        .setFooter(String.format("Click on %s to quick subscribe to this game.", SUBSCRIBE), null)
                        .build();

                textChannel.sendMessage(me).queue(m -> {
                    m.addReaction(SUBSCRIBE).queue();
                    embeds.put(m.getId(), role);
                });

                message.delete().queue();
            }
        }


        if (event instanceof MessageReactionAddEvent) {
            MessageReactionAddEvent mrea = (MessageReactionAddEvent) event;
            //Check if this is a message we are monitoring. No guild checks etc required. Filter bots
            if (!mrea.getUser().isBot() && embeds.containsKey(mrea.getMessageId())) {
                //Subscribe a member if they aren't already.
                if (mrea.getReactionEmote().getName().equals(SUBSCRIBE)) {
                    Role role = embeds.get(mrea.getMessageId());
                    Member subscriber = bot.getGuild().getMember(mrea.getUser());
                    TextChannel textChannel = mrea.getTextChannel();

                    if (Objects.requireNonNull(subscriber).getRoles().contains(role)) {
                        subscriber.getUser().openPrivateChannel().queue(
                                priv -> priv.sendMessage(String.format("You are already subscribed to `%s`.", role.getName())).queue()
                        );
                    } else {
                        bot.getLtgHandler().joinGameRole(role, subscriber.getUser(),
                                success -> {
                                    String reply = String.format("`%s` is now subscribed to `%s`.", subscriber.getEffectiveName(), role.getName());
                                    textChannel.sendMessage(reply).queue();
                                    String logNSA = String.format("LTG | **%s** subscribed to __%s__", subscriber.getEffectiveName(), role.getName());
                                    bot.getNsaTxChannel().sendMessage(logNSA).queue();
                                },
                                failure -> textChannel.sendMessage(failure.getMessage()).queue());
                    }
                }
            }
        }
    }
}
