package com.yuchuan.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.yuchuan.Config.Config;
import com.yuchuan.libnet.GateWay;
import com.yuchuan.protocol.Cmd;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by haroldmiao on 2014/12/26.
 */
public class LoginService extends Service {
    private final IBinder mBinder = new LocalBinder();

    public Boolean getStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(Boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    Boolean stopFlag = true;
    GateWay gateway = null;
    private int queueSize = 10;

    private ArrayBlockingQueue<Cmd> loginQueue = new ArrayBlockingQueue<Cmd>(queueSize);

    public class LocalBinder extends Binder {
         public LoginService getService() {
            return LoginService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void doConnectGatway(ArrayBlockingQueue<Cmd> loginQueue) {
        try {
            gateway = new GateWay(Config.GATEWAY_ADDR, 17000, loginQueue);
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

    public void doProcess() {
        doConnectGatway(loginQueue);
        doGetMsgServer();

        while(stopFlag){
            try {
                Cmd cmd = loginQueue.take();
                Log.d("LoginService", cmd.toString());
                System.out.println("从队列取走一个元素，队列剩余"+loginQueue.size()+"个元素");
                switch (cmd.getCmdName()) {
                    case Cmd.SELECT_MSG_SERVER_FOR_CLIENT_CMD:
                        Intent intent = new Intent();
                        intent.putExtra(Cmd.SELECT_MSG_SERVER_FOR_CLIENT_CMD, (String)cmd.getArg(0));
                        intent.setAction("com.yuchuan.services.LoginService");
                        sendBroadcast(intent);
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            gateway.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
