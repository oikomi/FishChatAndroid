package com.yuchuan.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.yuchuan.services.LoginService;

import yuchuan.com.fishchat.R;

public class LoginActivity extends Activity {
    private Button bLogin = null;
    boolean mBound = false;
    LoginService mService;

    public void onVideo(View view) {
        VideoView mvdView = (VideoView) findViewById(R.id.videoView);
        String strPath = "http://172.16.205.50/aaaa.m3u8";
        Uri uri = Uri.parse(strPath);
        mvdView.setVideoURI(uri);
        mvdView.setMediaController(new MediaController(this));
        mvdView.start();
    }

    public void onQuit(View view) {
        finish();
    }
    public void onLogin(View view)  {
        Log.i("Login", "onLogin");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mService.doConnectGatway();
                mService.doGetMsgServer();
            }
        }).start();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i("Login", "onServiceConnected");
            LoginService.LocalBinder binder = (LoginService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bLogin = (Button) this.findViewById(R.id.login);
    }

    @Override
    protected void onStart() {
        Log.i("Login","onStart");
        super.onStart();
        Intent intent = new Intent(this, LoginService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

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
