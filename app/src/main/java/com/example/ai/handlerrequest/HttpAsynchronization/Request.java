package com.example.ai.handlerrequest.HttpAsynchronization;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.example.ai.handlerrequest.util.CounterOutStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by AI on 2018/3/8.
 */

public abstract class Request<T> {

    private String boundary=createBoundary();
    private String startBoundary="--"+boundary;
    private String endBoundary=startBoundary+"--";

    /**
     * 请求地址
     */
    private String url;
    /**
     * 请求方法
     */
    private RequestMethod method;

    /**
     * 设置请求头
     */
    private Map<String,String> mRequestHead;
    /**
     * ContentType
     */
    private String mContentType;
    /**
     * 是否强制开启表单提交
     */
    private boolean enableFromData;
    /**
     * 请求参数
     */

    private List<KeyValue> keyValues;
    private String mCharset="utf-8";
    /**
     * SSL认证证书
     */
    private SSLSocketFactory mSslSocketFactory;
    /**
     * 服务器认证规则
     */
    private HostnameVerifier mHostnameVerifier;

    public Request(String url){
        this(url,RequestMethod.GET);
    }



    public Request(String url,RequestMethod method){
        this.url=url;
        this.method=method;
        /**
         * threshold：入口、门槛、阈值、开始、极限、临界值
         *HashMap 是一个散列表，它存储的内容是键值对(key-value)映射。
         *HashMap 继承于AbstractMap，实现了Map、Cloneable、java.io.Serializable接口。
         *HashMap 的实现不是同步的，这意味着它不是线程安全的。
         *它的key、value都可以为null。此外，HashMap中的映射不是有序的。
         */

        mRequestHead=new HashMap<>();
        keyValues=new ArrayList<>();
    }

    /**
     * 设置SSL证书
     * @param sslSocketFactory {@link SSLSocketFactory}
     */

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory){
        this.mSslSocketFactory=sslSocketFactory;
    }

    /**
     * 设置服务器主机认证规则
     * @param hostnameVerifier
     */
    public void setHostnameVerifier(HostnameVerifier hostnameVerifier){
        this.mHostnameVerifier=hostnameVerifier;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return mSslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return mHostnameVerifier;
    }

    /**
     * 拿到请求的完整的url
     * @return
     */
    public String getUrl() {
        //url?key=value...
        StringBuilder urlBuilder=new StringBuilder(url);
        String paramsBuilder=buildParamsString();
        if(!method.isOutputMethod()){
            //1.url:http://www.yanzhenjie.com?name=234
            if(paramsBuilder.length()>0&&url.contains("?")&&url.contains("=")){
                urlBuilder.append("&");
            }
            //2.http://www.yanzhenjie.com?
            else if (paramsBuilder.length()>0&&!url.endsWith("?")){
                urlBuilder.append("?");
            }
            urlBuilder.append(paramsBuilder);
        }
        return urlBuilder.toString();
    }

    public RequestMethod getMethod() {
        return method;
    }

    /**
     * 外部设置请求头
     * @param key 键
     * @param value 值
     */

    public void setHeader(String key,String value){
        mRequestHead.put(key,value);
    }

    /**
     * 拿到请求头
     * @return
     */
    Map<String,String> getRequestHeader(){
        return mRequestHead;
    }
    /**
     * ContentType要等用户设置好所有参数才会产生
     * 设置ContentType String值
     * 文件和String的ContentType是不一样
     * @param value
     */
    public void setContentType(String value){
        this.mContentType=value;
    }

    /**
     * 开发者设置提交参数的编码格式
     * @param mCharset
     */
    public void setCharset(String mCharset){
        this.mCharset=mCharset;

    }
    /**
     * 拿到ContentType
     * 文件只能通过模拟表单提交
     * @return
     */
    public String getContentType(){
        if(!TextUtils.isEmpty(mContentType)){
            //返回开发者设置的特殊ContentType
            return mContentType;
        }
        /**
         * 是否强制开启表单提交、是否文件(文件只能通过模拟表单和body提交)
         */
        else if(enableFromData||hasFile()){
            //提交表单的特殊contentType
            //Content-Type:multipart/form-data;boundary=--dads7u878ad6
            //------------------------------------------
            //表单中的string Item
            //--boundary(开始分割符)B(startBoundary)
            // Content-Disposition:form-data;name="keyName" //相当于key=value中的key;
            //Content-Type:text/plain; charset="utf-8"
            //
            //String数据.....
            //==========表单中的File Item============
            // --boundary
            // Content-Disposition:form-data;name="keyName";filename="dfg.jpg"
            //Content-Type:image/jpeg;
            //
            //file stream
            // --boundary--(结束分割符)
            return "multipart/form-data;boundary="+boundary;
        }
        return "application/x-www-form-urlencoded";

    }

    /**
     * 判断是否有文件
     * @return
     */
    protected boolean hasFile(){
        for(KeyValue keyValue:keyValues){
            Object value=keyValue.getValue();
            if(value instanceof File){
                return true;
            }
        }
        return false;
    }

    /**
     * 拿到包体的大小
     * @return
     */

    public long getContentLength(){
        //post类型的请求的时候才需要知道，一般都是上传文件的时候
        //普通数据的post不需要
        //form:1.普通String的表单 2.带文件的表单

        CounterOutStream counterOutStream=new CounterOutStream();
        try {
            onWriteBody(counterOutStream);
        } catch (Exception e) {
            return  0;
        }

        return counterOutStream.get();
    }

    /**
     * 写出包体的方法
     * @param outputStream
     * @throws Exception
     */

    public  void onWriteBody(OutputStream outputStream) throws IOException{
        if (enableFromData || hasFile()) {
            writeFormData(outputStream);
        } else {
            writeStringData(outputStream);
        }

    }

    /**
     * 写出普通数据
     * @param outputStream
     */
    private void writeStringData(OutputStream outputStream) throws IOException{
        String params=buildParamsString();
        outputStream.write(params.getBytes());
    }

    private void writeFormData(OutputStream outputStream) throws IOException{
        for(KeyValue keyValue:keyValues){
            String key=keyValue.getKey();
            Object value=keyValue.getValue();
            if(value instanceof File){//File
                writeFormFileData(outputStream,key,(File)value);
            }else{//string
                writeFormStringData(outputStream,key,(String)value);
            }

            outputStream.write("\r\n".getBytes());
        }

        outputStream.write(endBoundary.getBytes());
    }

    /**
     * 写出表单中的文件Item
     * @param outputStream
     * @param key
     * @param file
     */
    private void writeFormFileData(OutputStream outputStream,String key,File file) throws IOException{

        // --boundary
        // Content-Disposition:form-data;name="keyName";filename="dfg.jpg"
        //Content-Type:image/jpeg;
        //
        //file stream
        // boundary--(结束分割符)
        String fileName=file.getName();

        String mimeType="application/octet-stream";
        if(MimeTypeMap.getSingleton().hasExtension(fileName)){
            String extension=MimeTypeMap.getFileExtensionFromUrl(fileName);
            mimeType=MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        StringBuilder builder=new StringBuilder();

        builder.append(startBoundary)
                .append("\r\n");
        builder.append("Content-Disposition: form-data; name=\"")
                .append(key)
                .append("\"; filename=\"")
                .append(fileName)
                .append("\"")
                .append("\r\n");//换行

        builder.append("Content-Type: ").append(mimeType);


        builder.append("\r\n\r\n");

        outputStream.write(builder.toString().getBytes(mCharset));

        if(outputStream instanceof CounterOutStream){
            InputStream inputStream=new FileInputStream(file);
            ((CounterOutStream)outputStream).write(file.length());
        }else{
            InputStream inputStream=new FileInputStream(file);
            byte[] buffer=new byte[2048];
            int len;

            while((len=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,len);
            }
        }


    }

    /**
     * 写出文件中的String Item
     * @param outputStream
     * @param key
     * @param value
     */

    private void writeFormStringData(OutputStream outputStream,String key,String value) throws IOException{
        //--boundary(开始分割符)
        // Content-Disposition:form-data;name="keyName" //相当于key=value中的key;
        //Content-Type:text/plain; charset="utf-8"
        //
        //String数据.....
        StringBuilder builder=new StringBuilder();

        builder.append(startBoundary).append("\r\n");

        builder.append("Content-Disposition:form-data; name=\"")
                .append(key)
                .append("\"").append("\r\n");//换行

        builder.
                append("Content-Type:text/plain; charset=\"").
                append(mCharset).append("\"").append("\r\n");
        builder.append("\r\n\r\n");
        builder.append(value);
        outputStream.write(builder.toString().getBytes(mCharset));


    }



    /**
     * 是否强制开启表单提交
     * @param enable
     */
    public void fromData(boolean enable){
        /**
         * 能拿到输出流的方法才能提交表单
         */
        if(!method.isOutputMethod()){
            throw new IllegalArgumentException(method.value()+"is not support output");
        }
        enableFromData=enable;
    }

    protected List<KeyValue> getKeyValues() {
        return keyValues;
    }

    /**
     * 添加参数
     * @param key
     * @param value
     */

    public void add(String key, int value){
        keyValues.add(new KeyValue(key,Integer.toString(value)));
    }
    /**
     * 添加参数
     * @param key
     * @param value
     */

    public void add(String key,long value){
        keyValues.add(new KeyValue(key,Long.toString(value)));

    }
    /**
     * 添加参数
     * @param key
     * @param value
     */

    public void add(String key,String value){
        keyValues.add(new KeyValue(key,value));
    }

    /**
     * 添加文件
     * @param key
     * @param
     */

    public void add(String key,File value){
        keyValues.add(new KeyValue(key,value));
    }

    /**
     * List的toString()方法调用了其存储的每个元素的toString()方法。
     * @return
     */

    protected String createBoundary(){
        StringBuilder stringBuilder=new StringBuilder("--IMooc");
        stringBuilder.append(UUID.randomUUID());
        return stringBuilder.toString();
    }

    /**
     * 以key=value&key1=value1的形式构建用户添加的所有的string参数
     * @return
     */
    protected String buildParamsString(){
        StringBuilder stringBuilder=new StringBuilder();
        for(KeyValue keyValue:keyValues){
            Object value=keyValue.getValue();
            if(value instanceof String){
                //url?key=value&key1=value1;
                stringBuilder.append("&");

                try {
                    stringBuilder.append(URLEncoder.encode(keyValue.getKey(),mCharset));
                    stringBuilder.append("=");
                    stringBuilder.append(URLEncoder.encode((String)value,mCharset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        }
        if(stringBuilder.length()>0){
            //结果是：&key=value&key1=value1
            stringBuilder.deleteCharAt(0);
        }
        return stringBuilder.toString();
    }

    /**
     * 解析服务器的数据
     * @param responseBody
     * @return
     */

    public abstract T parseResponse(byte[] responseBody) throws Exception;
    /**
     * List的tostring()就是调用内部元素的tostring()方法
     * @return
     */
    @Override
    public String toString() {
        return "url:"+url+"=>method:"+method+"=>params:"+keyValues.toString();
    }

}
