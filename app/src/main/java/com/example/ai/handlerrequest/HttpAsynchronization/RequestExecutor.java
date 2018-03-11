package com.example.ai.handlerrequest.HttpAsynchronization;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by AI on 2018/3/8.
 */

public enum RequestExecutor{
    /**
     * enum枚举，是全局单例
     */
    INTANCE;
    /**
     * 创建一个单线程池
     * ExecutorService是线程池类
     */
    private  ExecutorService mExecutorService;
    /**
     * 用不到枚举的时候是不会调用构造方法
     */
    RequestExecutor(){
        mExecutorService=Executors.newSingleThreadExecutor();
    }

    /**
     * 执行一个请求
     * @param request 请求对象{@link Request}
     */
    /**
     * 线程池的execute方法会执行里面的线程
     * @param request
     */
    public void execute(Request request,HttpListener httpListener){
        mExecutorService.execute(new RequestTask(request,httpListener));
    }

}
