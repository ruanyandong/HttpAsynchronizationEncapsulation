package com.example.ai.handlerrequest.util;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by AI on 2018/3/9.
 */

public class CounterOutStream extends OutputStream{
    /**
     *AtomicLong是作用是对长整形进行原子操作。
     *在32位操作系统中，
     *64位的long 和 double 变量由于会被JVM当作两个分离的32位来进行操作，
     *所以不具有原子性。而使用AtomicLong能让long的操作保持原子型。
     */

    private AtomicLong atomicLong=new AtomicLong();

    /**
     * 拿到总长度
     * @return
     */
    public long get(){
        return atomicLong.get();
    }

    public CounterOutStream() {
        super();
    }


    public void write(long b) throws IOException {

        /**
         * 一个字节长度
         */
        atomicLong.addAndGet(b);
    }

    @Override
    public void write(int b) throws IOException {

        /**
         * 一个字节长度
         */
        atomicLong.addAndGet(1);
    }

    @Override
    public void write(@NonNull byte[] b) throws IOException {
        atomicLong.addAndGet(b.length);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        atomicLong.addAndGet(len);
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

}
