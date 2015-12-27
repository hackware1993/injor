package com.iflytek.main;

import android.app.Application;

import com.injor.main.SkinManager;

public class SkinApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		SkinManager.getInstance().init(this).addProcessor(new BrightnessProcessor());
	}
}
