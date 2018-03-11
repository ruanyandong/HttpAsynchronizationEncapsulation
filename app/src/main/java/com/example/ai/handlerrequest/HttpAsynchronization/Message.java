package com.example.ai.handlerrequest.HttpAsynchronization;

/**
 * Created by AI on 2018/3/8.
 */

public class Message implements Runnable{



    private Response response;

    private HttpListener httpListener;

    public Message(Response response,HttpListener httpListener) {
        this.response = response;
        this.httpListener=httpListener;
    }

    /**
     * 这里被回调到主线程
     */
    @Override
    public void run() {
        Exception exception=response.getException();

        if(exception!=null){
            httpListener.onFailed(exception);
        }else{
            httpListener.onSucceed(response);
        }

    }



}
