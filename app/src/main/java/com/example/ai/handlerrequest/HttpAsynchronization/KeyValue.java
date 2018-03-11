package com.example.ai.handlerrequest.HttpAsynchronization;

/**
 * Created by AI on 2018/3/8.
 */

import java.io.File;

/**
 * 请求参数类
 */

public class KeyValue {

    private String key;
    private Object value;

    public KeyValue(String key,String value){
        this.key=key;
        this.value=value;
    }

    public  KeyValue(String key, File value){
        this.key=key;
        this.value=value;
    }

    public String getKey(){
        return key;
    }

    public Object getValue(){
        return value;
    }

    @Override
    public String toString() {
        return "key:"+key+"===>value"+value;
    }
}
