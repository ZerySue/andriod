package com.example.admin.mylocation;

import android.app.Notification;
import android.util.Log;

import java.util.logging.Handler;

/**
 * Created by admin on 2016/10/29.
 */
public class AccessNetWork implements Runnable {
    private String op;
    private  String url;
    private  String params;
    private Handler h;
    public AccessNetWork(String op, String url, String params) {
        super();
        this.op=op;
        this.url=url;
        this.params=params;
        this.h=h;
    }
    public  void  run()
    {
       Message m=new Message();
        m.what(0x123);
        if(op.equals("GET"))
        {
            m.obj(HttpHelper.sendGet(url,params));
            Log.i("ddddddd", "run: "+m.obj());
        }
        if(op.equals("POST"))
        {
            m.obj(HttpHelper.sendPost(url,params));
            Log.i("ddddddd", "run: " + m.obj());
        }


    }
}
