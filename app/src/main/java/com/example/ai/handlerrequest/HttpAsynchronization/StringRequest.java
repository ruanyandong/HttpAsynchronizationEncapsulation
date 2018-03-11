package com.example.ai.handlerrequest.HttpAsynchronization;

/**
 * Created by AI on 2018/3/11.
 */

public class StringRequest extends Request<String> {
    public StringRequest(String url) {
        this(url,RequestMethod.GET);
    }

    public StringRequest(String url, RequestMethod method) {
        super(url, method);
        /**
         * 告诉服务器客户端能接收的数据类型
         */
        //setHeader("Accept","application/json");
        //setHeader("Accept","application/xml");
        setHeader("Accept","*");//接收全部类型
    }

    @Override
    public String parseResponse(byte[] responseBody) {
        return parseResponseString(responseBody);
    }


    public static String  parseResponseString(byte[] responseBody){
        if(responseBody!=null&&responseBody.length>0){
            return new String(responseBody);
        }
        return "";
    }


}
