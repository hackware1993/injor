package com.iflytek.main;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.injor.common.ResourceManager;
import com.injor.common.UITaskManager;
import com.injor.processor.ISkinProcessor;

public class BrightnessProcessor extends ISkinProcessor {
	private static final String DEFTYPE_INTEGER = "integer";
	public static final String PROCESSOR_BRIGHTNESS = "brightNess";

	@Override
	public void process(final Activity activity, View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		if (activity != null) {
			final LayoutParams lp = activity.getWindow().getAttributes();
			Resources currentResources = resourceManager.getCurrentResources();
			Resources defaultResources = resourceManager.getDefaultResources();
			float brightnessValue = -1f;
			int id = 0;
			try {
				id = currentResources.getIdentifier(resourceManager.appendSuffix(resName), DEFTYPE_INTEGER, resourceManager.getCurrentPluginPackageName());
				if (id != 0) {
					brightnessValue = (float) (currentResources.getInteger(id) / 100.0);
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (currentResources != defaultResources) {
					id = defaultResources.getIdentifier(resName, DEFTYPE_INTEGER, resourceManager.getDefaultPackageName());
					if (id != 0) {
						brightnessValue = (float) (defaultResources.getInteger(id) / 100.0);
					}
				}
			}
			lp.screenBrightness = brightnessValue;
			if (isInUiThread) {
				activity.getWindow().setAttributes(lp);
			} else {
				UITaskManager.getInstance().post(new Runnable() {
					@Override
					public void run() {
						activity.getWindow().setAttributes(lp);
					}
				});
			}
		}
	}

	@Override
	public String getName() {
		return PROCESSOR_BRIGHTNESS;
	}
}
