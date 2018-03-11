package com.example.ai.handlerrequest.HttpAsynchronization;

/**
 * Created by AI on 2018/3/8.
 */

public interface HttpListener<T> {
    /**
     * 请求成功
     * @param response 响应数据
     */
    void onSucceed(Response<T> response);

    /**
     * 请求失败
     * @param e 失败的异常
     */
    void onFailed(Exception e);
}
