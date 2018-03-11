package com.example.ai.handlerrequest.error;

import java.net.SocketTimeoutException;

/**
 * Created by AI on 2018/3/11.
 */

public class TimeOutError extends SocketTimeoutException{

    public TimeOutError(String msg) {
        super(msg);
    }

    public TimeOutError() {
    }

}
