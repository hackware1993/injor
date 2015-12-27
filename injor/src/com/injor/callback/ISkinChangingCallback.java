package com.injor.callback;

import android.util.Log;

import com.injor.main.SkinManager;

/**
 * 皮肤切换回调
 * 
 * @author hackware 2015.12.26
 * 
 */
public interface ISkinChangingCallback {

	/**
	 * 开始切换
	 */
	void onStart();

	/**
	 * 切换过程出错
	 * 
	 * @param e
	 *            异常信息
	 */
	void onError(Exception e);

	/**
	 * 切换完成（成功）
	 */
	void onComplete();

	// 默认的切换回调
	public static final ISkinChangingCallback DEFAULT = new ISkinChangingCallback() {

		@Override
		public void onStart() {
			Log.d(SkinManager.TAG, "onStart");
		}

		@Override
		public void onError(Exception e) {
			Log.d(SkinManager.TAG, "onError", e);
		}

		@Override
		public void onComplete() {
			Log.d(SkinManager.TAG, "onComplete");
		}
	};
}
