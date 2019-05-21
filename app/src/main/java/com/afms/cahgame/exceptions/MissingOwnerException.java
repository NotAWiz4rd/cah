package com.afms.cahgame.exceptions;

public class MissingOwnerException extends RuntimeException {
    public MissingOwnerException() {
       throw new MissingOwnerException("The img_card_white is missing it's owner!");
    }

    public MissingOwnerException(String message) {
        super(message);
    }
}
