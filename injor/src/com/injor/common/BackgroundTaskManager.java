package com.injor.common;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * 使用HandlerThread处理后台任务的工具类
 * 
 * @author hackware 2015.12.26
 * 
 */
public class BackgroundTaskManager {
	private BackgroundHandler backgroundHandler;

	private BackgroundTaskManager() {
		HandlerThread handlerThread = new HandlerThread("BackgroundTaskManager");
		handlerThread.start();
		backgroundHandler = new BackgroundHandler(handlerThread.getLooper());
	}

	private static class SingletonHolder {
		static BackgroundTaskManager sManager = new BackgroundTaskManager();
	}

	public static BackgroundTaskManager getInstance() {
		return SingletonHolder.sManager;
	}

	private static class BackgroundHandler extends Handler {
		public BackgroundHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}

	public void post(Runnable runnable) {
		backgroundHandler.post(runnable);
	}
}
