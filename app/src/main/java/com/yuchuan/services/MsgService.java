package com.yuchuan.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.yuchuan.libnet.MsgServer;
import com.yuchuan.protocol.Cmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by haroldmiao on 2015/2/12.
 */
public class MsgService extends Service {
    private final IBinder mBinder = new LocalBinder();
    MsgServer msgServer = null;

    private int queueSize = 10;

    private ArrayBlockingQueue<Cmd> msgQueue = new ArrayBlockingQueue<Cmd>(queueSize);
    private boolean stopFlag = true;

    public class LocalBinder extends Binder {
        public MsgService getService() {
            return MsgService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void doConnectMsgServer(String host, int port, ArrayBlockingQueue<Cmd> msgQueue) {
        Log.i("MsgService", "doConnectMsgServer");
        try {
            InetSocketAddress addr = new InetSocketAddress(host, port);
            msgServer = new MsgServer(addr, msgQueue);
            msgServer.connet();
        } catch (IOException e) {
            System.out.println("error.....");
            e.printStackTrace();
        }
    }

    public void doSendID() {
        Log.i("MsgService", "doSendID");
        try {
            msgServer.sendID();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void doProcess(String host, int port) {
        Log.i("MsgService", "doProcess");
        doConnectMsgServer(host, port, msgQueue);
        doSendID();

        while(stopFlag){
            try {
                Cmd cmd = msgQueue.take();
                Log.d("MsgService", cmd.toString());
                System.out.println("从MsgService队列取走一个元素，队列剩余" + msgQueue.size()+"个元素");
                switch (cmd.getCmdName()) {
                    case Cmd.RESP_MESSAGE_P2P_CMD:
                        Intent intent = new Intent();
                        //intent.putExtra(Cmd.RESP_MESSAGE_P2P_CMD, (String)cmd.getArg(0));
                        //intent.putExtra(Cmd.RESP_MESSAGE_P2P_CMD, (String)cmd.getArg(1));
                        intent.putExtra("cmd", cmd);
                        intent.setAction("com.yuchuan.services.MsgService");
                        sendBroadcast(intent);
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
