package com.injor.processor.impl;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ListView;

import com.injor.common.ResourceManager;
import com.injor.common.UITaskManager;
import com.injor.main.SkinManager;
import com.injor.processor.ISkinProcessor;

/**
 * 处理listview的divider
 * 
 * @author hackware 2015.12.26
 * 
 */
public class DividerProcessor extends ISkinProcessor {

	@Override
	public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		if (view instanceof ListView) {
			final Drawable divider = resourceManager.getDrawable(resName);
			if (divider != null) {
				final ListView listView = (ListView) view;
				if (isInUiThread) {
					listView.setDivider(divider);
				} else {
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
							listView.setDivider(divider);
						}
					});
				}
			}
		}
	}

	@Override
	public String getName() {
		return SkinManager.PROCESSOR_DIVIDER;
	}
}
