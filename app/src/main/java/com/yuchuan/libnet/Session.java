package com.yuchuan.libnet;

import android.util.Log;

import com.google.gson.Gson;
import com.yuchuan.protocol.Cmd;
import com.yuchuan.protocol.Protocol;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by haroldmiao on 2014/12/24.
 */

public class Session {
    private Gson gson = new Gson();
    private SocketChannel channel = null;
    private Selector selector;
    private SelectionKey selection_key;
    private final List<ByteBuffer> sendBufs = new LinkedList<ByteBuffer>();
    private ByteBuffer  receiveBuf = ByteBuffer.allocate(128 << 10);
    private long preSendTime;
    private long preReceiveTime;
    private Thread  ioThread;
    private boolean closeFlag;

    public Session() throws IOException {
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        receiveBuf.limit(0);
        receiveBuf.order(ByteOrder.BIG_ENDIAN);
    }

    public void connect(SocketAddress addr) throws IOException {
        receiveBuf.limit(0);
        receiveBuf.order(ByteOrder.BIG_ENDIAN);
        channel.connect(addr);
    }

    public void register(Selector selector) throws IOException {
        synchronized(this) {
            this.selector = selector;
            selection_key = channel.register(selector, interestOps());
        }
    }

    int interestOps() {
        int ops = SelectionKey.OP_READ;

        if (channel.isConnectionPending()) {
            ops |= SelectionKey.OP_CONNECT;
        }
        if (!sendBufs.isEmpty()) {
            ops |= SelectionKey.OP_WRITE;
        }

        return ops;
    }

    void finishConnect() throws IOException {
        channel.finishConnect();
    }

    synchronized void  close() throws IOException {
        closeFlag = true;
        channel.close();
    }

    void handleRead(SelectionKey key, ArrayBlockingQueue<Cmd> queue) throws IOException{
        //Log.d("Session", "handleRead");
        SocketChannel channel = (SocketChannel) key.channel();
        int pos = receiveBuf.position();
        receiveBuf.position(receiveBuf.limit());
        receiveBuf.limit(receiveBuf.capacity());

        int r = channel.read(receiveBuf);
        if (r < 0) {

        }

        if (r > 0) {
            preReceiveTime = System.currentTimeMillis();
        }

        receiveBuf.flip();
        receiveBuf.position(pos);

        while (receiveBuf.remaining() >= 4) {
            pos = receiveBuf.position();
            int size = receiveBuf.getInt();

            if (size > Protocol.MAX_PACKET_SIZE) {
                //throw new InvalidPacketException();
                //e.printStackTrace();
            }

            if (receiveBuf.remaining() < size) {
                receiveBuf.position(pos);

                if (receiveBuf.capacity() < size + 4) {
                    ByteBuffer buf = ByteBuffer.allocate(size + 4);
                    buf.order(ByteOrder.BIG_ENDIAN);
                    System.arraycopy(receiveBuf.array(), receiveBuf.position(), buf.array(), 0, receiveBuf.remaining());
                    buf.limit(receiveBuf.remaining());
                    receiveBuf = buf;
                }
                break;
            }

            int limit = receiveBuf.limit();
            receiveBuf.limit(receiveBuf.position() + size);

            try {
                Protocol p = new Protocol();
                p.decoder(receiveBuf, size, queue);
            }
            catch (Exception e) {
                //throw new InvalidPacketException();
                e.printStackTrace();
            }

            receiveBuf.limit(limit);
        }

        if (receiveBuf.position() != 0) {
            int remain = receiveBuf.remaining();
            System.arraycopy(receiveBuf.array(), receiveBuf.position(), receiveBuf.array(), 0, remain);
            receiveBuf.position(0);
            receiveBuf.limit(remain);
        }
    }

    synchronized void handleSend() throws IOException {
        //Log.d("Session", "handleSend");
        if (sendBufs.size() > 0) {
            long writeNum = channel.write(sendBufs.toArray(new ByteBuffer[sendBufs.size()]));
            Iterator<ByteBuffer> iter = sendBufs.iterator();

            while (iter.hasNext()) {
                ByteBuffer b = iter.next();

                if (b.remaining() == 0) {
                    iter.remove();
                }
                else {
                    break;
                }
            }

            if (sendBufs.isEmpty() && selection_key != null) {
                selection_key.interestOps(interestOps());
            }
        }
    }

    synchronized void sendPacket(Cmd cmd) throws IOException {
        //Log.d("Session", "sendPacket");
        boolean isEmpty = sendBufs.isEmpty();
        Protocol p = new Protocol();
        p.encoder(cmd);
        sendBufs.add(p.getBuf());

        if (isEmpty && selection_key != null) {
            selection_key.interestOps(interestOps());
            selector.wakeup();
        }

    }

    public void startIOThread(final ArrayBlockingQueue<Cmd> queue) {
        Runnable ioTask = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        selector.select(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Iterator ite = selector.selectedKeys().iterator();
                    while (ite.hasNext()) {
                        SelectionKey key = (SelectionKey) ite.next();
                        ite.remove();
                        if (key.isConnectable()) {
                            Log.d("Session", "isConnectable");
                            SocketChannel channel = (SocketChannel) key.channel();

                            if (channel.isConnectionPending()) {
                                try {
                                    channel.finishConnect();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            //try {
                            //    channel.configureBlocking(false);
                            //} catch (IOException e) {
                            //    e.printStackTrace();
                            //}
                            //try {
                            //    channel.register(selector, SelectionKey.OP_WRITE);
                            //} catch (ClosedChannelException e) {
                            //    e.printStackTrace();
                            //}
                        } else if (key.isReadable()) {
                            //Log.d("Session", "isReadable");
                            try {
                                handleRead(key, queue);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (key.isWritable()) {
                            Log.d("Session", "isWritable");
                            try {
                                handleSend();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        synchronized(this) {
                            if (closeFlag) {
                                break;
                            }
                        }

                    }

                }
            }
        };

        ioThread = new Thread(ioTask);
        ioThread.start();
    }
}
