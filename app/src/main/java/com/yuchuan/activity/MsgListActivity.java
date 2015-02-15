package com.yuchuan.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yuchuan.protocol.Cmd;
import com.yuchuan.services.MsgService;

import java.util.ArrayList;

import yuchuan.com.fishchat.R;

/**
 * Created by haroldmiao on 2015/2/12.
 */
public class MsgListActivity extends Activity {
    private ListView msgList = null;
    boolean mBound = false;
    MsgService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i("Msg", "onServiceConnected");
            MsgService.LocalBinder binder = (MsgService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Msg", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_list);
        msgList = (ListView) findViewById(R.id.msg_list);
        final Intent oldIntent = getIntent();

        findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String msgServerAddr = oldIntent.getStringExtra(Cmd.SELECT_MSG_SERVER_FOR_CLIENT_CMD);
                        Log.d("MsgListActivity ", msgServerAddr);
                        String[] tmp = msgServerAddr.split(":");
                        mService.doProcess(tmp[0], Integer.parseInt(tmp[1]));
                    }
                }).start();
            }
        });


        int[] images = new int[] { R.drawable.ic_launcher };

        ArrayList<MsgCellData> data = new ArrayList<MsgCellData>();
        for (int i = 0; i < 1; i++) {
            MsgCellData itemBean = new MsgCellData(images[0], "可爱的测试机器人",
                    "lala");
            data.add(itemBean);
        }

        // 绑定XML中的ListView，作为Item的容器

        MsgCellAdapter adapter = new MsgCellAdapter(MsgListActivity.this,
                R.layout.activity_msg_list_cell, data);
        msgList.setAdapter(adapter);

        msgList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(MsgListActivity.this, ChatActivity.class);

                //startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onStart() {
        Log.i("Msg","onStart");
        super.onStart();
        Intent intent = new Intent(MsgListActivity.this, MsgService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}
