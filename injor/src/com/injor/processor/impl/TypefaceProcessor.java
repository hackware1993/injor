package com.injor.processor.impl;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.injor.common.ResourceManager;
import com.injor.common.UITaskManager;
import com.injor.main.SkinManager;
import com.injor.processor.ISkinProcessor;

/**
 * 处理TextView的typeface，实现通过皮肤换字体
 * 
 * @author hackware 2015.12.26
 * 
 */
public class TypefaceProcessor extends ISkinProcessor {

	@Override
	public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		if (view instanceof TextView) {
			final Typeface typeface = resourceManager.getTypeface(resName);
			if (typeface != null) {
				final TextView textView = (TextView) view;
				if (isInUiThread) {
					textView.setTypeface(typeface);
				} else {
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
							textView.setTypeface(typeface);
						}
					});
				}
			}
		}
	}

	@Override
	public String getName() {
		return SkinManager.PROCESSOR_TYPEFACE;
	}
}
