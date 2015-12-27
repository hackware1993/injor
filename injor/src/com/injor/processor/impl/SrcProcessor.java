package com.injor.processor.impl;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.injor.common.ResourceManager;
import com.injor.common.UITaskManager;
import com.injor.main.SkinManager;
import com.injor.processor.ISkinProcessor;

/**
 * 处理ImageView的src
 * 
 * @author hackware 2015.12.26
 * 
 */
public class SrcProcessor extends ISkinProcessor {

	@Override
	public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		if (view instanceof ImageView) {
			final Drawable drawable = resourceManager.getDrawable(resName);
			if (drawable != null) {
				final ImageView imageView = (ImageView) view;
				if (isInUiThread) {
					imageView.setImageDrawable(drawable);
				} else {
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
							imageView.setImageDrawable(drawable);
						}
					});
				}
			}
		}
	}

	@Override
	public String getName() {
		return SkinManager.PROCESSOR_SRC;
	}
}
