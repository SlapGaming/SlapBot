package com.telluur.SlapBot.features.pun;

import net.dv8tion.jda.core.entities.Member;

/**
 * Punishment object that keeps track of current punishments.
 *
 * @author Rick Fontein
 */

public class Punishment {
    Member punMember;
    int timeout;

    public Punishment(Member punMember, int timeout) {
        this.punMember = punMember;
        this.timeout = timeout;
    }

    public Member getPunMember() {
        return punMember;
    }

    public void setPunMember(Member punMember) {
        this.punMember = punMember;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
