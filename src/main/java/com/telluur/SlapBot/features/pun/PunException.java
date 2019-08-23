package com.telluur.SlapBot.features.pun;

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