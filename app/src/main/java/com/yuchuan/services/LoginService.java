package com.yuchuan.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.yuchuan.config.Config;
import com.yuchuan.libnet.GateWay;

import java.io.IOException;
import java.util.Random;

/**
 * Created by haroldmiao on 2014/12/26.
 */
public class LoginService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private final Random mGenerator = new Random();
    private Config cfg;
    GateWay gateway = null;

    public class LocalBinder extends Binder {
         public LoginService getService() {
            return LoginService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void doConnectGatway() {
        cfg = Config.getInstance();
        try {
            gateway = new GateWay("192.168.159.169", 17000);
            gateway.connet();
        } catch (IOException e) {
            System.out.println("error.....");
            e.printStackTrace();
        }
    }
    public void doGetMsgServer() {
        try {
            gateway.getMsgServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
