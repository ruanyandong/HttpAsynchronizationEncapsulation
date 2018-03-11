package com.example.ai.handlerrequest.error;

/**
 * Created by AI on 2018/3/11.
 */

public class ParseError extends Exception{

    public ParseError() {
    }

    public ParseError(String message) {
        super(message);
    }

    public ParseError(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseError(Throwable cause) {
        super(cause);
    }


}
