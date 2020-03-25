package com.telluur.SlapBot.features.joinnotifier;

import com.vdurmont.emoji.EmojiParser;

import java.util.Random;

class JoinMessages {
    private static final Random random = new Random();
    private static final String JOIN_EMOJI = EmojiParser.parseToUnicode(":raising_hand:");
    private static final String[] messages = {
            "%s just joined the server - glhf!",
            "%s just joined. Everyone, look busy!",
            "%s just joined. Can I get a heal?",
            "%s joined. You must construct additional pylons.",
            "Welcome, %s. Stay awhile and listen.",
            "Welcome, %s. We were expecting you ( ͡° ͜ʖ ͡°)",
            "Welcome, %s. We hope you brought pizza.",
            "A wild %s appeared.",
            "Swoooosh. %s just landed.",
            "Brace yourselves. %s just joined the server.",
            "%s just joined. Hide your bananas.",
            "%s just slid into the server.",
            "A %s has spawned in the server.",
            "Big %s showed up!",
            "Where’s %s? In the server!",
            "%s hopped into the server. Kangaroo!!",
            "Challenger approaching - %s has appeared!",
            "It's a bird! It's a plane! Nevermind, it's just %s.",
            "We've been expecting you %s",
            "It's dangerous to go alone, take %s!",
            "%s has joined the server! It's super effective!",
            "Cheers, love! %s is here!",
            "%s is here, as the prophecy foretold.",
            "%s has arrived. Party's over.",
            "Ready player %s",
            "%s is here to kick butt and chew bubblegum. And %s is all out of gum.",
            "Hello. Is it %s you're looking for?",
            "%s has joined. Stay a while and listen!",
            "Roses are red, violets are blue, %s joined this server with you",
            "Welcome to the squeaky rubber ducky circlejerk, %s!",
            "Quickly! Welcome %s by throwing rubber duckies at them!"
    };

    public static String randomJoinMessage() {
        return String.format("%s %s", JOIN_EMOJI, messages[random.nextInt(messages.length)]);
    }

}
