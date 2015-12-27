package com.injor.processor.impl;

import android.view.View;
import android.widget.TextView;

import com.injor.common.ResourceManager;
import com.injor.common.UITaskManager;
import com.injor.main.SkinManager;
import com.injor.processor.ISkinProcessor;

/**
 * 处理TextView的text，通过皮肤来换文案貌似没什么用，不过如果换了一个比较活泼的皮肤，如果想让文案也活泼一点，这就派上用场了
 * 同时，这样可以实现动态切换语言包
 * 
 * @author hackware 2015.12.26
 * 
 */
public class TextProcessor extends ISkinProcessor {

	@Override
	public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		if (view instanceof TextView) {
			final String str = resourceManager.getString(resName);
			if (str != null) {
				final TextView textView = (TextView) view;
				if (isInUiThread) {
					textView.setText(str);
				} else {
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
							textView.setText(str);
						}
					});
				}
			}
		}
	}

	@Override
	public String getName() {
		return SkinManager.PROCESSOR_TEXT;
	}
}
