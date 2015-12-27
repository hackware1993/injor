package com.injor.common;

import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.injor.constant.SkinConfig;

/**
 * 记录当前皮肤,使得重启程序皮肤不变
 * 
 * @author hackware 2015.12.21
 * 
 */
public class PrefManager {
	private Context mContext;
	private SharedPreferences mSp;
	private static PrefManager sManager;
	private int apiLevel;

	private PrefManager(Context context) {
		mContext = context;
		mSp = mContext.getSharedPreferences(SkinConfig.SKIN_PREF_KEY, Context.MODE_PRIVATE);

		// 连获取apiLevel都要保证兼容性，是不是丧心病狂了
		try {
			apiLevel = Integer.valueOf(Build.VERSION.SDK);
		} catch (Exception e) {
			apiLevel = 0;
		}
	}

	public static PrefManager getInstance(Context context) {
		if (sManager == null) {
			synchronized (PrefManager.class) {
				if (sManager == null) {
					sManager = new PrefManager(context);
				}
			}
		}
		return sManager;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void clear() {
		SharedPreferences.Editor editor = mSp.edit().clear();
		if (apiLevel >= 9) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	public Integer getInt(String key, int defaultValue) {
		return mSp.getInt(key, defaultValue);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void setInt(String key, int value) {
		SharedPreferences.Editor editor = mSp.edit().putInt(key, value);
		if (apiLevel >= 9) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void setBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = mSp.edit().putBoolean(key, value);
		if (apiLevel >= 9) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return mSp.getBoolean(key, defaultValue);
	}

	public String getString(String key, String defaultValue) {
		return mSp.getString(key, defaultValue);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void setString(String key, String value) {
		SharedPreferences.Editor editor = mSp.edit().putString(key, value);
		if (apiLevel >= 9) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	public float getFloat(String key, float defaultValue) {
		return mSp.getFloat(key, defaultValue);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void setFloat(String key, float value) {
		SharedPreferences.Editor editor = mSp.edit().putFloat(key, value);
		if (apiLevel >= 9) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void setLong(String key, long value) {
		SharedPreferences.Editor editor = mSp.edit().putLong(key, value);
		if (apiLevel >= 9) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	public void getLong(String key, long defaultValue) {
		mSp.getLong(key, defaultValue);
	}

	// 下面这两个方法没什么卵用
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setStringSet(String key, Set<String> values) {
		SharedPreferences.Editor editor = mSp.edit().putStringSet(key, values);
		if (apiLevel >= 9) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Set<String> getStringSet(String key, Set<String> defaultValues) {
		return mSp.getStringSet(key, defaultValues);
	}
}
