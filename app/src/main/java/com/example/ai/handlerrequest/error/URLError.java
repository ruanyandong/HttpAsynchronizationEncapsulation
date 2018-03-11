package com.example.ai.handlerrequest.error;

import java.net.MalformedURLException;

/**
 * Created by AI on 2018/3/11.
 */

public class URLError extends MalformedURLException{

    public URLError() {
    }

    public URLError(String msg) {
        super(msg);
    }
}
