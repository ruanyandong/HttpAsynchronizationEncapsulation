package com.example.ai.handlerrequest.HttpAsynchronization;

import android.util.Log;

import com.example.ai.handlerrequest.error.ParseError;
import com.example.ai.handlerrequest.error.TimeOutError;
import com.example.ai.handlerrequest.error.URLError;
import com.example.ai.handlerrequest.error.UnknowHostError;
import com.example.ai.handlerrequest.urlconnection.URLConnectionFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by AI on 2018/3/8.
 */

public class RequestTask<T> implements Runnable{

    private Request<T> request;
    private HttpListener<T> httpListener;

    public RequestTask(Request<T> request,HttpListener<T> httpListener){
        this.request=request;
        this.httpListener=httpListener;
    }

    /**
     * 执行请求
     */
    @Override
    public void run(){

        /**
         * 开始执行
         */
        /**
         * 异常
         */
        Exception exception =null;
        /**
         * 响应码
         */
        int responseCode=-1;
        /**
         * 响应头session，一个key多个value
         */
        Map<String,List<String>> responseHeaders=null;

        byte[] responseBody=null;

        /**
         * 请求地址
         */
        String urlString=request.getUrl();
        /**
         * 请求方法
         */
        RequestMethod requestMethod=request.getMethod();
        /**
         * 没有try{}catch（）{}会出现异常MalformedURLException
         * malformed：畸形的，异常的，难看的
         */
        /**
         * 开始请求
         */
        HttpURLConnection httpURLConnection=null;
        try{
            /**
             * 1.建立连接
             */
            URL url=new URL(urlString);

            /**
             * 一句话切换OkHttp和UrlConnection
             */
            //用okHttp
            httpURLConnection= URLConnectionFactory.getInstance().openUrl(url);
            //用URLConnection
            // httpURLConnection=(HttpURLConnection)url.openConnection();
            /**
             * https的处理
             */
            if(httpURLConnection instanceof HttpsURLConnection){

                HttpsURLConnection httpsURLConnection=(HttpsURLConnection)httpURLConnection;

                SSLSocketFactory sslSocketFactory=request.getSslSocketFactory();

                if(sslSocketFactory!=null){
                    httpsURLConnection.setSSLSocketFactory(sslSocketFactory);//https证书相关信息
                }
                HostnameVerifier hostnameVerifier=request.getHostnameVerifier();
                if (hostnameVerifier!=null){
                    httpsURLConnection.setHostnameVerifier(hostnameVerifier);//服务器主机认证
                }
            }

            /**
             * 设置请求头的基础信息/设置请求头
             */
            httpURLConnection.setRequestMethod(requestMethod.value());
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(requestMethod.isOutputMethod());

            setHeader(httpURLConnection,request);
            /**
             * 2.发送数据
             */

            if(requestMethod.isOutputMethod()){
                OutputStream outputStream=httpURLConnection.getOutputStream();
                request.onWriteBody(outputStream);
            }

            /**
             * 3.读取响应
             */
            responseCode=httpURLConnection.getResponseCode();

            Log.d("ResponseCode", responseCode+"");
            responseHeaders=httpURLConnection.getHeaderFields();

            if (hasResponseBody(requestMethod,responseCode)){
                InputStream inputStream=getInputStream(httpURLConnection,responseCode);

                ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();


                int len;
                byte[] buffer=new byte[2048];

                while((len=inputStream.read(buffer))!=-1){
                   arrayOutputStream.write(buffer,0,len);
                }

                arrayOutputStream.close();

                responseBody=arrayOutputStream.toByteArray();
            }else{
                Log.d("没有响应包体", "run: ");
            }

        }catch(SocketTimeoutException e){
            exception=new TimeOutError("Timeout.");
        }catch(MalformedURLException e){//URL错误
            exception=new URLError("The url is error.");
        }catch(UnknownHostException e){
            exception=new UnknowHostError("The Server is not found.");
        }catch (IOException e){
            exception=e;
            e.printStackTrace();
        }finally{
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }

        //4.解析服务器数据
        T t=null;
        try {
            t=request.parseResponse(responseBody);
        } catch (Exception e) {
            exception=new ParseError("The data parse error");
        }

        /**
         * 执行结束
         */
        Response<T> response=new Response<T>(request,
                responseCode,
                responseHeaders,
                exception);
        response.setResponseBody(t);
        /**
         * 发送响应数据到主线程
         */
        Message message=new Message(response,httpListener);

        Poster.getInstance().post(message);
    }

    /**
     * 判断是否有包体
     * @param method 请求方法
     * @param responseCode  服务器的响应码
     * @return true ,other wise false
     */
    private boolean hasResponseBody(RequestMethod method,int responseCode){
        /**
         * http://www.w3school.com.cn/tags/html_ref_httpmessages.asp
         */
        return method!=RequestMethod.HEAD//HEAD请求没有包体
                && !(100<=responseCode&&responseCode<200)
                && responseCode != 204 && responseCode!=205
                && !(300 <=responseCode&&responseCode<400);
    }

    /**
     * 根据响应码拿到服务器的流
     * @param httpURLConnection
     * @param responseCode
     * @return
     */
    private InputStream getInputStream(HttpURLConnection httpURLConnection,int responseCode) throws IOException{

        InputStream inputStream;

        if(responseCode>=400){
            inputStream=httpURLConnection.getErrorStream();
        }else{
            inputStream=httpURLConnection.getInputStream();
        }
        /**
         * 拿到的流可能被服务器压缩，这里需要解压
         */
        String ContentEncoding=httpURLConnection.getContentEncoding();

        if(ContentEncoding!=null&&ContentEncoding.contains("gzip")){
            inputStream=new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    /**
     * 给URLConnection设置请求头
     * @param httpURLConnection
     * @param request
     */
    private void setHeader(HttpURLConnection httpURLConnection,
                           Request request){
        Map<String,String> requestHeader=request.getRequestHeader();
        //处理ContentType
        String contentType=request.getContentType();
        requestHeader.put("Content-Type",contentType);
        //处理Content-Length
        long contentLength=request.getContentLength();
        requestHeader.put("Content-Length",Long.toString(contentLength));


        for(Map.Entry<String,String> stringStringEntry: requestHeader.entrySet()){
            String headKey=stringStringEntry.getKey();
            String headValue=stringStringEntry.getValue();

            Log.d("setHeader",headKey+"="+headValue);

            httpURLConnection.setRequestProperty(headKey,headValue);
        }
    }
    /**
     *keySet()方法返回值是Map中key值的集合；
     * entrySet()的返回值也是返回一个Set集合，此集合的类型为Map.Entry。
     *Map.Entry是Map声明的一个内部接口，此接口为泛型，定义为Entry<K,V>。它表示Map中的一个实体（一个key-value对）。接口中有getKey(),getValue方法。
     */
    /**
     * 因为Map这个类没有继承Iterable接口
     * 所以不能直接通过map.iterator来遍历(list，set就是实现了这个接口，
     * 所以可以直接这样遍历),所以就只能先转化为set类型，用entrySet()方法，
     * 其中set中的每一个元素值就是map中的一个键值对，
     * 也就是Map.Entry<K,V>了，然后就可以遍历了。
     */


}
