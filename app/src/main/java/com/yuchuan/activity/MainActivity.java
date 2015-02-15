package com.yuchuan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.yuchuan.Config.Config;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String clientID = Config.getCachedName(this);
		String password = Config.getCachedPassword(this);
		
		if (clientID != null && password != null) {
			Intent i =new Intent(this, ChatMainActivity.class);
			i.putExtra(Config.KEY_NAME, clientID);
			i.putExtra(Config.KEY_PASSWORD, password);
			startActivity(i);
		} else {
			startActivity(new Intent(this, LoginActivity.class));
		}
		
		finish();
	}


}
