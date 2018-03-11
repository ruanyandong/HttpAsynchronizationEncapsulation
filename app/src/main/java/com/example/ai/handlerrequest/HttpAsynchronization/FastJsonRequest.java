package com.example.ai.handlerrequest.HttpAsynchronization;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by AI on 2018/3/11.
 */

public class FastJsonRequest extends Request<JSONObject> {

    public FastJsonRequest(String url) {
        this(url,RequestMethod.GET);
    }

    public FastJsonRequest(String url, RequestMethod method) {
        super(url, method);
    }

    @Override
    public JSONObject parseResponse(byte[] responseBody) throws Exception{
        String result=StringRequest.parseResponseString(responseBody);
        return JSON.parseObject(result);

    }


}
