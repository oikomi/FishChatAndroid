package com.yuchuan.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yuchuan.protocol.Cmd;
import com.yuchuan.services.LoginService;

import yuchuan.com.fishchat.R;

public class LoginActivity extends Activity {
    private Button bLogin = null;
    private EditText etName = null;
    private EditText etPassword = null;
    boolean mBound = false;
    LoginService mService;
    private LoginResReceiver receiver;

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
        bLogin = (Button) this.findViewById(R.id.signin_button);
        etName = (EditText) findViewById(R.id.username_edit);
        etPassword = (EditText) findViewById(R.id.password_edit);

        receiver = new LoginResReceiver();
        IntentFilter filter = new IntentFilter("com.yuchuan.services.LoginService");
        registerReceiver(receiver, filter);


        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(etName.getText())) {
                    Toast.makeText(LoginActivity.this, "user name can_not_be_empty", Toast.LENGTH_LONG).show();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mService.doProcess();
                    }
                }).start();
            }
        });
    }


    public class LoginResReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("LoginResReceiver", "onReceive");
            String msgAddr = intent.getStringExtra(Cmd.SELECT_MSG_SERVER_FOR_CLIENT_CMD);
            Log.d("LoginResReceiver", msgAddr);

            Intent i = new Intent(LoginActivity.this, ChatMainActivity.class);

            i.putExtra(Cmd.SELECT_MSG_SERVER_FOR_CLIENT_CMD, msgAddr);

            startActivity(i);
            finish();
        }
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
        unregisterReceiver(receiver);
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

}
