package com.injor.processor.impl;

import android.view.View;
import android.widget.TextView;

import com.injor.common.ResourceManager;
import com.injor.common.UITaskManager;
import com.injor.main.SkinManager;
import com.injor.processor.ISkinProcessor;

/**
 * 处理TextView的textSize
 * 
 * @author hackware 2015.12.26
 * 
 */
public class TextSizeProcessor extends ISkinProcessor {

	@Override
	public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		if (view instanceof TextView) {
			try {
				final float dimen = resourceManager.getDimension(resName);
				final TextView textView = (TextView) view;
				if (isInUiThread) {
					textView.setTextSize(dimen);
				} else {
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
							textView.setTextSize(dimen);
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
		return SkinManager.PROCESSOR_TEXT_SIZE;
	}
}
