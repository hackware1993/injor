package com.iflytek.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.iflytek.skintest.R;
import com.injor.callback.ISkinChangingCallback;
import com.injor.common.PrefManager;
import com.injor.main.SkinManager;
import com.injor.processor.ISkinProcessor;

public class SettingActivity extends Activity implements OnClickListener {

	private Button asyncLoadBtn;
	private EditText buttonNumEdit;
	private LinearLayout featureArea;
	private ISkinChangingCallback skinChangingCallback = new ISkinChangingCallback() {
		private ProgressDialog progressDialog;
		private long start;

		@Override
		public void onStart() {
			start = System.currentTimeMillis();
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(SettingActivity.this);
				progressDialog.setTitle(getString(R.string.changing_skin));
				progressDialog.setCancelable(false);
				progressDialog.setCanceledOnTouchOutside(false);
			}
			progressDialog.show();
		}

		@Override
		public void onError(Exception e) {
			progressDialog.dismiss();
		}

		@Override
		public void onComplete() {
			Toast.makeText(SettingActivity.this, getString(R.string.time_usage) + (System.currentTimeMillis() - start) + " ms", Toast.LENGTH_SHORT).show();
			progressDialog.dismiss();
		}
	};
	public static final String BUTTON_NUM = "button_num";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		SkinManager.getInstance().register(this);
		asyncLoadBtn = (Button) findViewById(R.id.enable_async_load_skin);
		asyncLoadBtn.setText(SkinManager.getInstance().isAsyncLoadSkin() ? R.string.enable_async_load_skin : R.string.disable_async_load_skin);
		buttonNumEdit = (EditText) findViewById(R.id.button_num_edit);
		buttonNumEdit.setText("" + PrefManager.getInstance(this).getInt(BUTTON_NUM, 0));
		featureArea = (LinearLayout) findViewById(R.id.feature_area);
		int marginTop = dip2px(5);
		for (ISkinProcessor processor : SkinManager.getInstance().getProcessors()) {
			Button button = new Button(this);
			String feature = processor.getName();
			if (SkinManager.getInstance().isProcessorDisabled(feature)) {
				button.setText(getString(R.string.disabled_feature) + feature);
			} else {
				button.setText(getString(R.string.enabled_feature) + feature);
			}
			button.setTag("skin:btn_color:textColor");
			button.setOnClickListener(this);
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, marginTop, 0, 0);
			featureArea.addView(button, layoutParams);
			SkinManager.getInstance().injectSkinAsync(button);
		}
	}

	public int dip2px(double dpValue) {
		return (int) (dpValue * getResources().getDisplayMetrics().density + 0.5);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.load_white_skin:
			SkinManager.getInstance().loadInternalSkin("", skinChangingCallback);
			break;
		case R.id.load_dark_skin:
			SkinManager.getInstance().loadInternalSkin("dark", skinChangingCallback);
			break;
		case R.id.enable_async_load_skin:
			SkinManager.getInstance().setAsyncLoadEnable(!SkinManager.getInstance().isAsyncLoadSkin());
			asyncLoadBtn.setText(SkinManager.getInstance().isAsyncLoadSkin() ? R.string.enable_async_load_skin : R.string.disable_async_load_skin);
			break;
		case R.id.button_num_setting:
			int buttonNum = 0;
			try {
				buttonNum = Integer.parseInt(buttonNumEdit.getText().toString());
				if (buttonNum >= 0) {
					PrefManager.getInstance(this).setInt(BUTTON_NUM, buttonNum);
				}
			} catch (Exception e) {
			}
			break;
		default:
			if (v instanceof Button) {
				Button button = (Button) v;
				String text = button.getText().toString();
				String disabledFeatureStr = getString(R.string.disabled_feature);
				String enabledFeatureStr = getString(R.string.enabled_feature);
				if (text.startsWith(disabledFeatureStr)) {
					String feature = text.replace(disabledFeatureStr, "");
					SkinManager.getInstance().enableProcessor(feature);
					button.setText(enabledFeatureStr + feature);
				} else if (text.startsWith(enabledFeatureStr)) {
					String feature = text.replace(enabledFeatureStr, "");
					SkinManager.getInstance().disableProcessor(feature);
					button.setText(disabledFeatureStr + feature);
				}
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SkinManager.getInstance().unregister(this);
	}
}
