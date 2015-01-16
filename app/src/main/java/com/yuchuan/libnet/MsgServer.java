package com.yuchuan.libnet;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;

/**
 * Created by haroldmiao on 2015/1/16.
 */
public class MsgServer {
    private InetSocketAddress addr;
    private Selector selector;
    private Session session;
    public MsgServer(InetSocketAddress addr) throws IOException {
        this.addr = addr;
        this.selector = Selector.open();
    }

    public void connet() throws IOException {
        Log.d("MsgServer", "connet");
        this.session = new Session();
        session.connect(addr);
        session.register(selector);
        session.startIOThread();
    }

}
