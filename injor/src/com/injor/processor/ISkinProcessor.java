package com.injor.processor;

import android.app.Activity;
import android.view.View;

import com.injor.common.ResourceManager;

/**
 * 皮肤处理器，每个处理器可处理一个resType，处理器由框架调用，扩展框架时只需实现不同的处理器并注册到框架
 * 
 * @author hackware 2015.12.26
 * 
 */
public abstract class ISkinProcessor {
	/**
	 * 为特定view换肤，这些参数均由框架传入
	 * 
	 * @param activity
	 *            当前view所在的activity，传入activity是为了让扩展性更强，比如通过皮肤调整界面亮度
	 * @param view
	 *            要换肤的view
	 * @param resName
	 *            资源名字
	 * @param resourceManager
	 *            用于获取资源
	 * @param isInUiThread
	 *            处理器是否运行在UI线程中，如果不是，则更改view时要使用UITaskManager.post(Runnable
	 *            runnable);
	 */
	public void process(Activity activity, View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		process(view, resName, resourceManager, isInUiThread);
	}

	public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
	}

	/**
	 * 返回处理器要处理的resType
	 * 
	 * @return
	 */
	public abstract String getName();
}
