package com.example.ai.handlerrequest.HttpAsynchronization;

import java.util.List;
import java.util.Map;

/**
 * Created by AI on 2018/3/8.
 */

public class Response<T> {


    /**
     * 请求对象
     */
    private Request request;

    /**
     * 服务器的响应码
     */
    private int responseCode;
    /**
     * 服务器的响应结果
     */
    private T responseBody;
    /**
     * 请求过程中发生的错误
     */
    private Exception exception;

    /**
     *服务器的响应头
     */
    private Map<String,List<String>> responseHeader;

    public Response(Request request,int responseCode,
                    Map<String,List<String>> responseHeader, Exception exception) {
        this.request=request;
        this.responseCode = responseCode;
        this.responseHeader=responseHeader;

        this.exception = exception;

    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public int getResponseCode() {
        return responseCode;
    }

    /**
     * 拿到响应结果
     * public byte[] getResponseBody(){
       return responseBody;}
     * @return
     */
    public T get(){
        return responseBody;
    }


    /**
     * 设置响应
     * @param responseBody
     */
    void setResponseBody(T responseBody){
        this.responseBody=responseBody;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, List<String>> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(Map<String, List<String>> responseHeader) {
        this.responseHeader = responseHeader;
    }




    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
