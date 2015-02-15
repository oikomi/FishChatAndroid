package com.yuchuan.protocol;

import android.util.Log;

import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by haroldmiao on 2014/12/26.
 */
public class Protocol {
    private Gson gson = new Gson();
    public static final int MAX_PACKET_SIZE = 256 << 20;
    public static final int DefaultProtocolSize = 4;
    private ByteBuffer buf;


    public Protocol() {

    }

    public void encoder(Cmd cmd) {
        Log.i("Protocol", "encoder");
        String data = gson.toJson(cmd);
        Log.i("Protocol", data);
        int size = data.getBytes().length;
        Log.i("Protocol", String.valueOf(size));
        buf = ByteBuffer.allocate(size + DefaultProtocolSize);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putInt(size);
        buf.put(data.getBytes());
        buf.position(0);
    }

    public Cmd decoder(ByteBuffer buf, int size, ArrayBlockingQueue<Cmd> queue) throws InterruptedException {
        Log.d("Protocol", "decoder");
        //Log.d("Protocol", new String(buf.array()));
        Cmd cmd = new Cmd();
        //buf.getInt();
        byte b[] = new byte[size];

        buf.get(b);
        Log.i("Protocol", new String(b));
        cmd = gson.fromJson(new String(b), Cmd.class);
        queue.put(cmd);
        //Log.d("Protocol", cmd.toString());
        buf.position(buf.limit());

        return cmd;
    }

    public ByteBuffer getBuf() {
        return buf;
    }

    public void parseProtocol(String cmd) {
        Cmd c = gson.fromJson(cmd, Cmd.class);
        Log.i("parseProtocol", c.toString());
    }

}


