package com.example.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class PreMainActivity extends Activity {
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pre_main);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				change();
			}
		}, 150);
	}

	protected void change() {
		Intent intent = new Intent(PreMainActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
