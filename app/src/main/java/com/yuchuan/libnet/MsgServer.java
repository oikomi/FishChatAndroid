package com.yuchuan.libnet;

import android.util.Log;

import com.yuchuan.protocol.Cmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by haroldmiao on 2015/1/16.
 */
public class MsgServer {
    private InetSocketAddress addr;
    private Selector selector;
    private Session session;
    private int queueSize = 100;

    private ArrayBlockingQueue<Cmd> msgQueue = new ArrayBlockingQueue<Cmd>(queueSize);
    public MsgServer(InetSocketAddress addr, ArrayBlockingQueue<Cmd> msgQueue) throws IOException {
        this.addr = addr;
        this.msgQueue = msgQueue;
        this.selector = Selector.open();
    }

    public void connet() throws IOException {
        Log.d("MsgServer", "connet");
        this.session = new Session();
        session.connect(addr);
        session.register(selector);
        session.startIOThread(msgQueue);
    }

    public void sendID() throws IOException {
        Log.d("MsgServer", "sendID");
        Cmd cmd = new Cmd();
        cmd.setCmdName(Cmd.SEND_CLIENT_ID_CMD);
        cmd.addArg("az");
        this.session.sendPacket(cmd);
    }

}
