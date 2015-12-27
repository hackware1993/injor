package com.injor.common;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.injor.main.SkinManager;

/**
 * 用于获取资源，无论内置的还是插件中的，都是使用android.content.res.Resources类
 * 
 * @author hackware 2015.12.20
 * 
 */
public class ResourceManager {
	private static final String DEFTYPE_DRAWABLE = "drawable";
	private static final String DEFTYPE_COLOR = "color";
	private static final String DEFTYPE_STRING = "string";
	private static final String DEFTYPE_DIMEN = "dimen";

	// 用于访问内置资源
	private Resources mDefaultResources = SkinManager.getInstance().getContext().getResources();
	private String mDefaultPackageName = SkinManager.getInstance().getContext().getPackageName();

	// 用于访问资源（内置或插件）
	private Resources mCurrentResources;
	private String mCurrentPluginPackageName; // Resources.getIdentifer(,,mCurrentPluginPackageName);

	// 皮肤后缀
	private String mSuffix;

	public ResourceManager(Resources res, String pluginPackageName, String suffix) {
		mCurrentResources = res;
		mCurrentPluginPackageName = pluginPackageName;
		mSuffix = suffix;
		if (mSuffix == null) {
			mSuffix = "";// 表示内置默认皮肤（插件或内置）
		}
	}

	public Resources getCurrentResources() {
		return mCurrentResources;
	}

	public Resources getDefaultResources() {
		return mDefaultResources;
	}

	public String getSuffix() {
		return mSuffix;
	}

	public String getDefaultPackageName() {
		return mDefaultPackageName;
	}

	public String getCurrentPluginPackageName() {
		return mCurrentPluginPackageName;
	}

	public void setSuffix(String suffix) {
		mSuffix = suffix;
		if (mSuffix == null) {
			mSuffix = "";
		}
	}

	public String appendSuffix(String name) {
		if (!TextUtils.isEmpty(mSuffix)) {
			return name + "_" + mSuffix;
		}
		return name;
	}

	/*
	 * 下面的几个方法作用都是根据名字获取资源
	 * 先从当前的Resources中获取，如果获取不到
	 * 再从默认的Resources中获取，说白了就是在皮肤插件中
	 * 找不到资源的话，从内置资源中找，这也就能实现通过皮肤来
	 * 更改单一类型的资源，比如字体
	 */
	public Drawable getDrawable(String name) {
		try {
			return mCurrentResources.getDrawable(mCurrentResources.getIdentifier(appendSuffix(name), DEFTYPE_DRAWABLE, mCurrentPluginPackageName));
		} catch (Exception e) {
			e.printStackTrace();
			if (mCurrentResources != mDefaultResources) {
				try {
					return mDefaultResources.getDrawable(mDefaultResources.getIdentifier(name, DEFTYPE_DRAWABLE, mDefaultPackageName));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return null;
		}
	}

	public int getColor(String name) throws Exception {
		try {
			return mCurrentResources.getColor(mCurrentResources.getIdentifier(appendSuffix(name), DEFTYPE_COLOR, mCurrentPluginPackageName));
		} catch (Exception e) {
			if (mCurrentResources != mDefaultResources) {
				return mDefaultResources.getColor(mDefaultResources.getIdentifier(name, DEFTYPE_COLOR, mDefaultPackageName));
			}
			throw e;
		}
	}

	public ColorStateList getColorStateList(String name) {
		try {
			return mCurrentResources.getColorStateList(mCurrentResources.getIdentifier(appendSuffix(name), DEFTYPE_COLOR, mCurrentPluginPackageName));
		} catch (Exception e) {
			e.printStackTrace();
			if (mCurrentResources != mDefaultResources) {
				try {
					return mDefaultResources.getColorStateList(mDefaultResources.getIdentifier(name, DEFTYPE_COLOR, mDefaultPackageName));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return null;
		}
	}

	public String getString(String name) {
		try {
			return mCurrentResources.getString(mCurrentResources.getIdentifier(appendSuffix(name), DEFTYPE_STRING, mCurrentPluginPackageName));
		} catch (Exception e) {
			e.printStackTrace();
			if (mCurrentResources != mDefaultResources) {
				try {
					return mDefaultResources.getString(mDefaultResources.getIdentifier(name, DEFTYPE_STRING, mDefaultPackageName));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return null;
		}
	}

	public float getDimension(String name) throws Exception {
		try {
			return mCurrentResources.getDimension(mCurrentResources.getIdentifier(appendSuffix(name), DEFTYPE_DIMEN, mCurrentPluginPackageName));
		} catch (Exception e) {
			e.printStackTrace();
			if (mCurrentResources != mDefaultResources) {
				return mDefaultResources.getDimension(mDefaultResources.getIdentifier(name, DEFTYPE_DIMEN, mDefaultPackageName));
			}
			throw e;
		}
	}

	/**
	 * 获取字体，字体需要放置在assets/fonts目录下
	 * 
	 * @param name
	 *            字体文件的名字
	 * @return
	 */
	public Typeface getTypeface(String name) {
		String key;
		String param;
		CacheManager cacheManager = CacheManager.getInstance();
		try {
			param = "fonts/" + appendSuffix(name) + ".ttf";
			key = ((mCurrentResources == mDefaultResources) ? "typeface_internal://" : "typeface_plugin://") + param;
			Object object = cacheManager.get(key);
			if (object != null) {
				return (Typeface) object;
			} else {
				Typeface typeface = Typeface.createFromAsset(mCurrentResources.getAssets(), param);
				cacheManager.put(key, typeface);
				return typeface;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (mCurrentResources != mDefaultResources) {
				try {
					param = "fonts/" + name + ".ttf";
					key = "typeface_internal://" + param;
					Object object = cacheManager.get(key);
					if (object != null) {
						return (Typeface) object;
					} else {
						Typeface typeface = Typeface.createFromAsset(mDefaultResources.getAssets(), param);
						cacheManager.put(key, typeface);
						return typeface;
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return null;
		}
	}
}
