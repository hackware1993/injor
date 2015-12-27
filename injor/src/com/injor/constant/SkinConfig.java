package com.injor.constant;

/**
 * 常量，配置信息
 * 
 * @author hackware 2015.12.20
 * 
 */
public class SkinConfig {
	public static final String SKIN_PREF_KEY = "skin_plugin_pref";
	public static final String SKIN_STATEMENT_PREFIX = "skin:";

	public static final String KEY_CURRENT_SKIN_PATH = "key_current_skin_path";
	public static final String KEY_CURRENT_SKIN_PKG = "key_current_skin_pkg";
	public static final String KEY_CURRENT_SKIN_SUFFIX = "key_skin_plugin_file_suffix";
	public static final String KEY_ASYNC_LOAD_SKIN_SWITCH = "key_async_load_skin_switch";

	// 默认开启异步换肤，对于包含大量item（成千上万）的界面，换肤时界面也会保持响应，可在换肤时弹出进度框
	public static final boolean DEFAULT_ASYNC_LOAD_VALUE = true;
}
