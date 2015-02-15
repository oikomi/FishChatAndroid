package com.yuchuan.libnet;

import android.util.Log;

import com.yuchuan.protocol.Cmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by haroldmiao on 2014/12/24.
 */
public class GateWay {
    private InetSocketAddress addr;
    private Selector selector;
    private Session session;
    private ArrayBlockingQueue<Cmd> loginQueue;

    public GateWay(String host, int port, ArrayBlockingQueue<Cmd> loginQueue) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(host, port);
        this.addr = addr;
        this.loginQueue = loginQueue;
        this.selector = Selector.open();
    }

    public void connet() throws IOException {
        Log.d("GateWay", "connet");
        this.session = new Session();
        session.connect(addr);
        session.register(selector);
        session.startIOThread(loginQueue);
    }

    public void getMsgServer() throws IOException {
        Log.d("GateWay", "getMsgServer");
        Cmd cmd = new Cmd();
        cmd.setCmdName(Cmd.REQ_MSG_SERVER_CMD);
        this.session.sendPacket(cmd);

    }

    public void getResp() {
        Log.d("GateWay", "getResp");

    }

    public void close() throws IOException {
        this.session.close();
    }
}
