package com.example.ai.handlerrequest.HttpAsynchronization;

import android.content.pm.PackageInfo;

/**
 * Created by AI on 2018/3/8.
 */

public enum RequestMethod {
    GET("GET"),

    POST("POST"),

    HEAD("HEAD"),

    DELETE("DELETE");

    private String value;

    RequestMethod(String value){
        this.value=value;
    }


    public String value(){
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean isOutputMethod(){
        switch(this){
            /**
             * 允许输出数据，可以拿到outPutStream的类型
             */
            case POST:
            case DELETE:
                return true;
            default:
                return false;
        }
    }


}
