package com.example.ai.handlerrequest.error;

/**
 * Created by AI on 2018/3/11.
 */

public class UnknowHostError extends Exception {
    public UnknowHostError() {
    }

    public UnknowHostError(String message) {
        super(message);
    }

    public UnknowHostError(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknowHostError(Throwable cause) {
        super(cause);
    }


}
