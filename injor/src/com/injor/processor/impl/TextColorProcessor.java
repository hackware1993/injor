package com.injor.processor.impl;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import com.injor.common.ResourceManager;
import com.injor.common.UITaskManager;
import com.injor.main.SkinManager;
import com.injor.processor.ISkinProcessor;

/**
 * 处理TextView的textColor
 * 
 * @author hackware 2015.12.26
 * 
 */
public class TextColorProcessor extends ISkinProcessor {

	@Override
	public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		if (view instanceof TextView) {
			final ColorStateList colorList = resourceManager.getColorStateList(resName);
			if (colorList != null) {
				final TextView textView = (TextView) view;
				if (isInUiThread) {
					textView.setTextColor(colorList);
				} else {
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
							textView.setTextColor(colorList);
						}
					});
				}
			}
		}
	}

	@Override
	public String getName() {
		return SkinManager.PROCESSOR_TEXT_COLOR;
	}
}
