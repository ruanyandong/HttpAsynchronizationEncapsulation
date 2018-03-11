package com.example.ai.handlerrequest.HttpAsynchronization;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by AI on 2018/3/7.
 */

/**
 * 懒汉式单例模式
 */
public class Poster extends Handler {

    private static Poster instance;

    private Poster(){
        /**
         * 子线程可以和主线程发消息
         */
        super(Looper.getMainLooper());
    }

    public static Poster getInstance(){
        if(instance==null){
            synchronized(Poster.class){
                if(instance==null){
                    instance=new Poster();
                }
            }
        }
        return instance;
    }

}
