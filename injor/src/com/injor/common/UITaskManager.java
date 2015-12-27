package com.injor.common;

import android.os.Handler;
import android.os.Looper;

/**
 * 用于在子线程中更新UI。 异步刷新皮肤时，遍历contentView、解析skin tag以及加载资源
 * 都在子线程中，但最终将新资源设置到view时必须要在主线程中
 * 
 * @author hackware 2015.12.22
 * 
 */
public class UITaskManager extends Handler {
	private UITaskManager() {
		super(Looper.getMainLooper());
	}

	private static class SingletonHolder {
		static UITaskManager sManager = new UITaskManager();
	}

	public static UITaskManager getInstance() {
		return SingletonHolder.sManager;
	}
}
