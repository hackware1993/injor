package com.injor.main;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.injor.callback.ISkinChangingCallback;
import com.injor.common.BackgroundTaskManager;
import com.injor.common.PrefManager;
import com.injor.common.ResourceManager;
import com.injor.common.UITaskManager;
import com.injor.constant.SkinConfig;
import com.injor.entity.SkinItem;
import com.injor.processor.ISkinProcessor;
import com.injor.processor.impl.BackroundProcessor;
import com.injor.processor.impl.DividerProcessor;
import com.injor.processor.impl.SrcProcessor;
import com.injor.processor.impl.TextColorProcessor;
import com.injor.processor.impl.TextProcessor;
import com.injor.processor.impl.TextSizeProcessor;
import com.injor.processor.impl.TypefaceProcessor;

public class SkinManager {
	private static final String PROCESSOR_STATUS = "processor_enabled:";
	public static final String TAG = "SkinManager";
	private Context mContext;
	private ResourceManager mResourceManager;
	private PrefManager prefManager;

	private List<Activity> mActivities = new ArrayList<Activity>();// 需要换肤的activity
	private byte[] mLock = new byte[0];

	public static final String PROCESSOR_TYPEFACE = "typeface";
	public static final String PROCESSOR_SRC = "src";
	public static final String PROCESSOR_BACKGROUND = "background";
	public static final String PROCESSOR_TEXT_COLOR = "textColor";
	public static final String PROCESSOR_DIVIDER = "divider";
	public static final String PROCESSOR_TEXT_SIZE = "textSize";
	public static final String PROCESSOR_TEXT = "text";

	// 处理皮肤异步加载
	private boolean isAsyncLoadSkin;
	private volatile int mAsyncLoadIndex;
	private int mTotalActivities;
	private ISkinChangingCallback mSkinChangingCallback;
	private volatile boolean mIsChangingSkin;

	// 可以控制启用、禁用一些处理器
	private List<String> disabledProcessors = new ArrayList<String>();
	private List<ISkinProcessor> processors = new ArrayList<ISkinProcessor>();

	private SkinManager() {
		// 默认启用所有内置处理器
		processors.add(new BackroundProcessor());
		processors.add(new DividerProcessor());
		processors.add(new SrcProcessor());
		processors.add(new TextColorProcessor());
		processors.add(new TextProcessor());
		processors.add(new TextSizeProcessor());
		processors.add(new TypefaceProcessor());
	}

	private static class SingletonHolder {
		static SkinManager sInstance = new SkinManager();
	}

	public static SkinManager getInstance() {
		return SingletonHolder.sInstance;
	}

	public boolean isAsyncLoadSkin() {
		return prefManager.getBoolean(SkinConfig.KEY_ASYNC_LOAD_SKIN_SWITCH, SkinConfig.DEFAULT_ASYNC_LOAD_VALUE);
	}

	public void setCurrentSkinSuffix(String suffix) {
		prefManager.setString(SkinConfig.KEY_CURRENT_SKIN_SUFFIX, suffix);
	}

	public String getCurrentSkinSuffix() {
		return prefManager.getString(SkinConfig.KEY_CURRENT_SKIN_SUFFIX, "");
	}

	public String getCurrentSkinPackage() {
		return prefManager.getString(SkinConfig.KEY_CURRENT_SKIN_PKG, "");
	}

	public void setCurrentSkinPackage(String pkgName) {
		prefManager.setString(SkinConfig.KEY_CURRENT_SKIN_PKG, pkgName);
	}

	public void setCurrentSkinPath(String path) {
		prefManager.setString(SkinConfig.KEY_CURRENT_SKIN_PATH, path);
	}

	public String getCurrentSkinPath() {
		return prefManager.getString(SkinConfig.KEY_CURRENT_SKIN_PATH, "");
	}

	public void clearSkinConfig() {
		setCurrentSkinPath(null);
		setCurrentSkinPackage(null);
		setCurrentSkinSuffix(null);
	}

	public SkinManager init(Context context) {
		mContext = context;
		prefManager = PrefManager.getInstance(mContext);
		isAsyncLoadSkin = isAsyncLoadSkin();

		for (ISkinProcessor processor : processors) {
			if (!prefManager.getBoolean(PROCESSOR_STATUS + processor.getName(), true)) {
				disabledProcessors.add(processor.getName());
			}
		}

		// 获取皮肤信息
		String skinPluginPath = getCurrentSkinPath();
		String skinPluginPkg = getCurrentSkinPackage();
		String suffix = getCurrentSkinSuffix();

		if (!validPluginParams(skinPluginPath, skinPluginPkg)) {// 是否是内置皮肤
			mResourceManager = new ResourceManager(mContext.getResources(), mContext.getPackageName(), suffix);
		} else {
			try {
				// 生成插件的Resources
				mResourceManager = getPluginResourceManager(skinPluginPath, skinPluginPkg, suffix);
			} catch (Exception e) {
				e.printStackTrace();
				// 插件的Resources生成失败，切换到内置的默认皮肤
				mResourceManager = new ResourceManager(mContext.getResources(), mContext.getPackageName(), null);
				clearSkinConfig();
			}
		}
		return this;
	}

	public List<ISkinProcessor> getProcessors() {
		return processors;
	}

	/**
	 * 添加处理器，外部可覆盖内置处理器
	 * 
	 * @param newProcessor
	 */
	public void addProcessor(ISkinProcessor newProcessor) {
		boolean replaced = false;
		for (int i = 0; i < processors.size(); i++) {
			if (processors.get(i).getName().equals(newProcessor.getName())) {
				processors.set(i, newProcessor);
				replaced = true;
			}
		}
		if (!replaced) {
			processors.add(newProcessor);
		}
	}

	/**
	 * 启用处理器
	 * 
	 * @param processorName
	 */
	public void enableProcessor(String processorName) {
		boolean hasProcessor = false;
		for (ISkinProcessor processor : processors) {
			if (processor.getName().equals(processorName)) {
				hasProcessor = true;
				break;
			}
		}
		if (hasProcessor) {
			int index = disabledProcessors.indexOf(processorName);
			if (index != -1) {
				disabledProcessors.remove(index);
				prefManager.setBoolean(PROCESSOR_STATUS + processorName, true);
			}
		} else {
			throw new IllegalArgumentException("processor: '" + processorName + "' does't support.");
		}
	}

	/**
	 * 禁用处理器
	 * 
	 * @param processorName
	 */
	public void disableProcessor(String processorName) {
		boolean hasProcessor = false;
		for (ISkinProcessor processor : processors) {
			if (processor.getName().equals(processorName)) {
				hasProcessor = true;
				break;
			}
		}
		if (hasProcessor) {
			if (!disabledProcessors.contains(processorName)) {
				disabledProcessors.add(processorName);
				prefManager.setBoolean(PROCESSOR_STATUS + processorName, false);
			}
		} else {
			throw new IllegalArgumentException("processor: '" + processorName + "' does't support.");
		}
	}

	/**
	 * 禁用多个处理器
	 * 
	 * @param processorNames
	 */
	public void disableProcessors(String... processorNames) {
		for (int i = 0; i < processorNames.length; i++) {
			disableProcessor(processorNames[i]);
		}
	}

	public boolean isProcessorDisabled(String processorName) {
		return disabledProcessors.contains(processorName);
	}

	public List<String> getEnabledProcessors() {
		List<String> enabledProcessors = new ArrayList<String>();
		for (ISkinProcessor processor : processors) {
			if (!isProcessorDisabled(processor.getName())) {
				enabledProcessors.add(processor.getName());
			}
		}
		return enabledProcessors;
	}

	/**
	 * 获取外部apk的包名
	 * 
	 * @param skinPluginPath
	 * @return
	 */
	private PackageInfo getPackageInfo(String skinPluginPath) {
		PackageManager pm = mContext.getPackageManager();
		return pm.getPackageArchiveInfo(skinPluginPath, PackageManager.GET_ACTIVITIES);
	}

	/**
	 * 生成皮肤插件的资源管理器
	 * 
	 * @param skinPath
	 *            插件路径
	 * @param skinPkgName
	 *            插件包名
	 * @param suffix
	 *            插件中的皮肤后缀为null或""表示默认皮肤
	 * @return
	 * @throws Exception
	 */
	private ResourceManager getPluginResourceManager(String skinPath, String skinPkgName, String suffix) throws Exception {
		AssetManager assetManager = AssetManager.class.newInstance();
		Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
		addAssetPath.invoke(assetManager, skinPath);
		Resources superRes = mContext.getResources();
		Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
		return new ResourceManager(resources, skinPkgName, suffix);
	}

	private boolean validPluginParams(String skinPath, String skinPkgName) {
		if (TextUtils.isEmpty(skinPath) || TextUtils.isEmpty(skinPkgName)) {
			return false;
		}

		File file = new File(skinPath);
		if (!file.exists()) {
			return false;
		}

		PackageInfo info = getPackageInfo(skinPath);
		if (info == null || !skinPkgName.equals(info.packageName)) {
			return false;
		}
		return true;
	}

	/**
	 * 当前皮肤是否是内置的默认皮肤
	 * 
	 * @return
	 */
	public boolean isDefaultSkin() {
		return isInternalSkin() && TextUtils.isEmpty(mResourceManager.getSuffix());
	}

	/**
	 * 当前皮肤是否是插件皮肤
	 * 
	 * @return
	 */
	public boolean isPluginSkin() {
		return mContext.getResources() != mResourceManager.getCurrentResources();
	}

	/**
	 * 当前皮肤是否是插件中的默认皮肤
	 * 
	 * @return
	 */
	public boolean isPluginDefaultSkin() {
		return isPluginSkin() && TextUtils.isEmpty(mResourceManager.getSuffix());
	}

	/**
	 * 当前皮肤是否是内置皮肤
	 * 
	 * @return
	 */
	public boolean isInternalSkin() {
		return !isPluginSkin();
	}

	/**
	 * 设置异步换肤
	 * 
	 * @param is
	 */
	public void setAsyncLoadEnable(boolean is) {
		if (mIsChangingSkin) {
			throw new IllegalStateException("skin is changing.");
		} else {
			isAsyncLoadSkin = is;
			prefManager.setBoolean(SkinConfig.KEY_ASYNC_LOAD_SKIN_SWITCH, is);
		}
	}

	public ResourceManager getResourceManager() {
		return mResourceManager;
	}

	public Context getContext() {
		return mContext;
	}

	/**
	 * 加载内置皮肤
	 * 
	 * @param suffix
	 */
	public void loadInternalSkin(String suffix) {
		loadInternalSkin(suffix, null);
	}

	/**
	 * 加载内置皮肤，并监听回调
	 * 
	 * @param suffix
	 * @param callback
	 */
	public void loadInternalSkin(String suffix, ISkinChangingCallback callback) {
		if (callback == null) {
			callback = ISkinChangingCallback.DEFAULT;
		}
		if (mIsChangingSkin) {// 换肤过程中不能再换肤
			callback.onError(new IllegalStateException("skin is changing."));
			return;
		}
		mIsChangingSkin = true;
		callback.onStart();
		try {
			if (isPluginSkin()) {// 由插件皮肤切换到内置皮肤，需要重新生成ResourceManager
				mResourceManager = new ResourceManager(mContext.getResources(), mContext.getPackageName(), suffix);
			} else {
				String sfx = suffix;
				if (sfx == null) {
					sfx = "";
				}
				if (sfx.equals(mResourceManager.getSuffix())) {// 要切换的皮肤就是当前皮肤，不予切换
					mIsChangingSkin = false;
					callback.onComplete();
					return;
				}
				mResourceManager.setSuffix(suffix);
			}
			clearSkinConfig();
			setCurrentSkinSuffix(suffix);
			loadSkinInternel();// 执行真正的换肤操作
			if (!isAsyncLoadSkin) {// 如果不是异步换肤，则loadSkinInternal执行完后就换肤结束了
				mIsChangingSkin = false;
				callback.onComplete();
			} else {
				mSkinChangingCallback = callback;// 异步换肤，先保存回调
			}
		} catch (Exception e) {
			e.printStackTrace();
			mIsChangingSkin = false;
			callback.onError(e);
		}
	}

	/**
	 * 加载插件中的默认皮肤
	 * 
	 * @param skinPluginPath
	 *            插件路径
	 * @param skinPluginPkg
	 *            插件报名
	 * @param callback
	 *            换肤回调
	 */
	public void loadPluginSkin(String skinPluginPath, String skinPluginPkg, ISkinChangingCallback callback) {
		loadPluginSkin(skinPluginPath, skinPluginPkg, null, callback);
	}

	/**
	 * 加载插件中的默认皮肤，不提供回调
	 * 
	 * @param skinPluginPath
	 *            插件路径
	 * @param skinPluginPkg
	 *            插件报名
	 */
	public void loadPluginSkin(String skinPluginPath, String skinPluginPkg) {
		loadPluginSkin(skinPluginPath, skinPluginPkg, null);
	}

	/**
	 * 根据suffix选择插件内某套皮肤，默认为""
	 * 
	 * @param skinPluginPath
	 * @param skinPluginPkg
	 * @param suffix
	 * @param callback
	 */
	public void loadPluginSkin(String skinPluginPath, String skinPluginPkg, String suffix, ISkinChangingCallback callback) {
		if (callback == null) {
			callback = ISkinChangingCallback.DEFAULT;
		}
		if (mIsChangingSkin) {
			callback.onError(new IllegalStateException("skin is changing."));
			return;
		}
		mIsChangingSkin = true;
		callback.onStart();
		if (validPluginParams(skinPluginPath, skinPluginPkg)) {
			try {
				mResourceManager = getPluginResourceManager(skinPluginPath, skinPluginPkg, suffix);
				setCurrentSkinPath(skinPluginPath);
				setCurrentSkinPackage(skinPluginPkg);
				setCurrentSkinSuffix(suffix);
				loadSkinInternel();
				if (!isAsyncLoadSkin) {
					mIsChangingSkin = false;
					callback.onComplete();
				} else {
					mSkinChangingCallback = callback;
				}
			} catch (Exception e) {
				e.printStackTrace();
				mIsChangingSkin = false;
				callback.onError(e);
			}
		} else {
			mIsChangingSkin = false;
			callback.onError(new IllegalArgumentException("skin plugin params error, please check it."));
		}
	}

	/**
	 * 为某个activity换肤
	 * 
	 * @param activity
	 */
	public void apply(Activity activity) {
		injectSkin(activity, activity.findViewById(android.R.id.content));
	}

	/**
	 * 根据resType，查找对应的处理器
	 * 
	 * @param resType
	 * @return
	 */
	private ISkinProcessor getProcessor(String resType) {
		for (ISkinProcessor processor : processors) {
			if (processor.getName().equals(resType) && !isProcessorDisabled(resType)) {
				return processor;
			}
		}
		return null;
	}

	/**
	 * 解析皮肤语法 skin:resName:resType|skin:resName:resType|skin:resName:resType|...
	 * 
	 * @param tagStr
	 * @return
	 */
	private List<SkinItem> parseTag(String tagStr) {
		if (tagStr == null || tagStr.trim().length() == 0) {
			return null;
		}
		List<SkinItem> skinItems = new ArrayList<SkinItem>();
		String[] items = tagStr.split("\\|");
		for (String item : items) {
			if (!item.startsWith(SkinConfig.SKIN_STATEMENT_PREFIX)) {
				continue;
			}
			String[] resItems = item.split(":");
			if (resItems.length != 3) {
				continue;
			}
			SkinItem attr = new SkinItem(resItems[1], resItems[2]);
			skinItems.add(attr);
		}
		return skinItems;
	}

	/**
	 * 异步为某个activity换肤
	 * 
	 * @param activity
	 */
	private void applyAsync(final Activity activity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				apply(activity);
				mAsyncLoadIndex++;
				if (mAsyncLoadIndex == mTotalActivities) {
					mIsChangingSkin = false;
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
							mSkinChangingCallback.onComplete();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 注册activity，在Activity.onCreate中setContentView之后调用
	 * 
	 * @param activity
	 */
	public void register(Activity activity) {
		synchronized (mLock) {
			mActivities.add(activity);
		}
		apply(activity);
	}

	/**
	 * 注册activity，在Activity.onCreate中setContentView之后调用
	 * 
	 * @param activity
	 */
	public void registerAsync(Activity activity) {
		synchronized (mLock) {
			mActivities.add(activity);
		}
		applyAsync(activity);
	}

	/**
	 * 反注册activity，在Activity.onDestroy中调用
	 * 
	 * @param activity
	 */
	public void unregister(Activity activity) {
		synchronized (mLock) {
			mActivities.remove(activity);
		}
	}

	private void loadSkinInternel() {
		if (isAsyncLoadSkin) {
			synchronized (mLock) {
				mTotalActivities = mActivities.size();
				mAsyncLoadIndex = 0;
				for (Activity activity : mActivities) {
					applyAsync(activity);
				}
			}
		} else {
			synchronized (mLock) {
				for (Activity activity : mActivities) {
					apply(activity);
				}
			}
		}
	}

	/**
	 * 为单个view换肤
	 * 
	 * @param activity
	 * @param view
	 */
	private void injectSkin(Activity activity, View view) {
		Object tag = view.getTag();
		String tagStr = null;
		if (tag instanceof String) {
			tagStr = (String) tag;
		}
		boolean isInUiThread = (Looper.myLooper() == Looper.getMainLooper());
		if (tagStr != null) {
			List<SkinItem> skinItems = parseTag(tagStr);
			for (SkinItem skinItem : skinItems) {
				ISkinProcessor processor = getProcessor(skinItem.resType);
				if (processor != null) {
					processor.process(activity, view, skinItem.resName, mResourceManager, isInUiThread);
				}
			}
		}
		if (view instanceof ViewGroup) {
			ViewGroup container = (ViewGroup) view;
			for (int i = 0, n = container.getChildCount(); i < n; i++) {
				View child = container.getChildAt(i);
				injectSkin(activity, child);
			}
		}
	}

	/**
	 * 为单个view换肤，用于为动态生成的view换肤
	 * 
	 * @param view
	 */
	public void injectSkin(View view) {
		injectSkin(null, view);
	}

	public void injectSkinAsync(final View view) {
		BackgroundTaskManager.getInstance().post(new Runnable() {
			@Override
			public void run() {
				injectSkin(view);
			}
		});
	}
}
