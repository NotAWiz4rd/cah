package com.afms.cahgame.exceptions;

public class MissingOwnerException extends RuntimeException {
    public MissingOwnerException() {
       throw new MissingOwnerException("The card is missing it's owner!");
    }

    public MissingOwnerException(String message) {
        super(message);
    }
}
