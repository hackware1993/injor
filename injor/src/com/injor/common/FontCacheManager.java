package com.injor.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Typeface;

/**
 * 字体缓存管理器，避免同一字体重复加载
 * 
 * @author hackware 2015.12.22
 * 
 */
public class FontCacheManager {

	// 需要保证线程安全，换肤时多个Activity是同时换的，多个换肤线程可能会同时访问
	private Map<String, Typeface> mCache = new ConcurrentHashMap<String, Typeface>();

	private FontCacheManager() {
	}

	private static class SingletonHolder {
		static FontCacheManager sCacheManager = new FontCacheManager();
	}

	public static FontCacheManager getInstance() {
		return SingletonHolder.sCacheManager;
	}

	public Typeface get(String key) {
		return mCache.get(key);
	}

	public void put(String key, Typeface typeface) {
		if (typeface != null) {
			mCache.put(key, typeface);
		}
	}
}
