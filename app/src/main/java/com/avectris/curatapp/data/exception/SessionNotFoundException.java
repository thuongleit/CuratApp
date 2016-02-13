package com.avectris.curatapp.data.exception;

/**
 * Created by thuongle on 2/13/16.
 */
public class SessionNotFoundException extends Throwable {

    public SessionNotFoundException() {
    }

    public SessionNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public SessionNotFoundException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    public SessionNotFoundException(Throwable cause) {
        super(cause);
    }

    public SessionNotFoundException(String detailMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(detailMessage, cause, enableSuppression, writableStackTrace);
    }
}
