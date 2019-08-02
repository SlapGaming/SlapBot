package com.telluur.SlapBot.pun;

/**
 * Pun Exception placeholder
 *
 * @author Rick Fontein
 */

public class PunException extends Exception {

    public PunException(String errorMessage) {
        super(errorMessage);
    }

    public PunException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}