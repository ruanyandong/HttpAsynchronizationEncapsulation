package com.example.ai.handlerrequest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.ai.handlerrequest.HttpAsynchronization.HttpListener;
import com.example.ai.handlerrequest.HttpAsynchronization.Request;
import com.example.ai.handlerrequest.HttpAsynchronization.RequestExecutor;
import com.example.ai.handlerrequest.HttpAsynchronization.RequestMethod;
import com.example.ai.handlerrequest.HttpAsynchronization.Response;
import com.example.ai.handlerrequest.HttpAsynchronization.StringRequest;
import com.example.ai.handlerrequest.error.ParseError;
import com.example.ai.handlerrequest.error.TimeOutError;
import com.example.ai.handlerrequest.error.URLError;

import java.io.File;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.GET).setOnClickListener(this);
        findViewById(R.id.POST).setOnClickListener(this);
        findViewById(R.id.HEAD).setOnClickListener(this);
        findViewById(R.id.DELETE).setOnClickListener(this);
    }

    /**
     * 执行GET请求
     * GET请求不能传送文件
     */

    private void requestGet(){
        String rootPath= Environment.getExternalStorageDirectory().getAbsolutePath();

        final Request<String> request=new StringRequest("http://192.168.1.226/IMoocHttpServer/upload", RequestMethod.GET);
        request.add("name","严振杰");
        request.add("pwd",123456);

        RequestExecutor.INTANCE.execute(request, new HttpListener<String>() {


            @Override
            public void onFailed(Exception e) {

                Log.d("异常", e+"");
                if(e instanceof ParseError){
                    //数据解析异常
                }else if(e instanceof TimeOutError){
                    //超时
                }else if(e instanceof UnknownHostException){
                    //没找到服务器
                }else if(e instanceof URLError){
                    //URL格式错误
                }

            }

            @Override
            public void onSucceed(Response<String> response) {

                String str=response.get();

                Log.d("结果===》",
                        "onSucceed: "+response.getResponseCode()+"\n"+str);
            }
        });
    }

    /**
     * 执行Post请求
     */
    private void requestPost(){
        String rootPath= Environment.getExternalStorageDirectory().getAbsolutePath();
        File file1=new File(rootPath+"/hyman.png");
        File file2=new File(rootPath+"/hyman.png");
        File file3=new File(rootPath+"/hyman.png");
        File file4=new File(rootPath+"/hyman.png");
        File file5=new File(rootPath+"/hyman.png");
        File file6=new File(rootPath+"/hyman.png");

        final Request<String> request=new StringRequest("http://192.168.1.226/IMoocHttpServer/upload", RequestMethod.POST);
        request.add("name","严振杰");
        request.add("pwd",123456);

        /**
         * key全部相同也可以
         */
        request.add("image1",file1);
        request.add("image2",file2);
        request.add("image3",file3);
        request.add("image4",file4);
        request.add("image5",file5);
        request.add("image6",file6);
        RequestExecutor.INTANCE.execute(request, new HttpListener<String>() {


            @Override
            public void onFailed(Exception e) {

                Log.d("异常", e+"");
                if(e instanceof ParseError){
                    //数据解析异常
                }else if(e instanceof TimeOutError){
                    //超时
                }else if(e instanceof UnknownHostException){
                    //没找到服务器
                }else if(e instanceof URLError){
                    //URL格式错误
                }

            }

            @Override
            public void onSucceed(Response<String> response) {

                String str=response.get();

                Log.d("结果===》",
                        "onSucceed: "+response.getResponseCode()+"\n"+str);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.GET:{
                requestGet();
                break;
            }
            case R.id.POST:{
                requestPost();
                break;
            }
            case R.id.HEAD:{
                break;
            }

            case R.id.DELETE:{
                break;
            }
        }

    }
}
