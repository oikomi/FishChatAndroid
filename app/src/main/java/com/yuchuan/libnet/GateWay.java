package com.yuchuan.libnet;

import android.util.Log;

import com.yuchuan.protocol.Cmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;

/**
 * Created by haroldmiao on 2014/12/24.
 */
public class GateWay {
    private InetSocketAddress addr;
    private Selector selector;
    private Session session;

    public GateWay(String host, int port) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(host, port);
        this.addr = addr;
        this.selector = Selector.open();
    }

    public void connet() throws IOException {
        Log.d("GateWay", "connet");
        this.session = new Session();
        session.connect(addr);
        session.register(selector);
        session.startIOThread();
    }

    public void getMsgServer() throws IOException {
        Log.d("GateWay", "getMsgServer");
        Cmd cmd = new Cmd();
        cmd.setCmdName(Cmd.REQ_MSG_SERVER_CMD);
        this.session.sendPacket(cmd);
    }
}
