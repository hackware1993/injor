package com.injor.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存管理器，避免资源重复加载
 * 
 * @author hackware 2015.12.22
 * 
 */
public class CacheManager {

	// 需要保证线程安全，换肤时多个Activity是同时换的，多个换肤线程可能会同时访问
	private Map<String, Object> mCache = new ConcurrentHashMap<String, Object>();

	private CacheManager() {
	}

	private static class SingletonHolder {
		static CacheManager sCacheManager = new CacheManager();
	}

	public static CacheManager getInstance() {
		return SingletonHolder.sCacheManager;
	}

	public Object get(String key) {
		return mCache.get(key);
	}

	public void put(String key, Object content) {
		if (content != null) {
			mCache.put(key, content);
		}
	}
}
