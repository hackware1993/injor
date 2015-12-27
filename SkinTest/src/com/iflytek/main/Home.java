package com.iflytek.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.iflytek.skintest.R;
import com.injor.callback.ISkinChangingCallback;
import com.injor.common.PrefManager;
import com.injor.main.SkinManager;

import java.io.File;

public class Home extends Activity {

    private ISkinChangingCallback skinChangingCallback = new ISkinChangingCallback() {
        private ProgressDialog progressDialog;
        private long start;

        @Override
        public void onStart() {
            start = System.currentTimeMillis();
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(Home.this);
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
            Toast.makeText(Home.this, getString(R.string.time_usage) + (System.currentTimeMillis() - start) + " ms", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SkinManager.getInstance().register(this);
        int buttonNum = PrefManager.getInstance(this).getInt(SettingActivity.BUTTON_NUM, 0);
        if (buttonNum > 0) {
            LinearLayout root = (LinearLayout) findViewById(R.id.root);
            int marginTop = dip2px(5);
            for (int i = 1; i <= buttonNum; i++) {
                Button button = new Button(this);
                button.setText(getString(R.string.dynamic_button) + " " + i);
                button.setTag("skin:btn_color:textColor|skin:btn_background:background|skin:font_size:textSize|skin:custom_font:typeface");
                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, marginTop, 0, 0);
                root.addView(button, layoutParams);
                SkinManager.getInstance().injectSkinAsync(button);
            }
        }
    }

    public int dip2px(double dpValue) {
        return (int) (dpValue * getResources().getDisplayMetrics().density + 0.5);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_white_skin:
                SkinManager.getInstance().loadInternalSkin("", skinChangingCallback);
                break;
            case R.id.load_dark_skin:
                SkinManager.getInstance().loadInternalSkin("dark", skinChangingCallback);
                break;
            case R.id.load_plugin_skin:
                SkinManager.getInstance().loadPluginSkin(Environment.getExternalStorageDirectory() + File.separator + "skin_plugin.skin", "com.iflytek.skin", skinChangingCallback);
                break;
            case R.id.start_new_activity:
                startActivity(new Intent(Home.this, SettingActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
