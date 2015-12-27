package com.injor.processor.impl;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.injor.common.ResourceManager;
import com.injor.common.UITaskManager;
import com.injor.main.SkinManager;
import com.injor.processor.ISkinProcessor;

/**
 * 处理background
 * 
 * @author hackware 2015.12.26
 * 
 */
public class BackroundProcessor extends ISkinProcessor {

	@Override
	public void process(final View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		final Drawable drawable = resourceManager.getDrawable(resName);
		if (drawable != null) {
			if (isInUiThread) {
				view.setBackgroundDrawable(drawable);
			} else {
				UITaskManager.getInstance().post(new Runnable() {
					@Override
					public void run() {
						view.setBackgroundDrawable(drawable);
					}
				});
			}
		} else {
			try {
				final int color = resourceManager.getColor(resName);
				if (isInUiThread) {
					view.setBackgroundColor(color);
				} else {
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
							view.setBackgroundColor(color);
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getName() {
		return SkinManager.PROCESSOR_BACKGROUND;
	}
}
