# injor
**injor** —— 一个高度可扩展的，非侵入式的，简单的，轻量的，强悍的，"原生"的Android皮肤框架，仅35K，无第三方依赖，完美兼容Android 1.0 ~ 6.0。支持内置皮肤和插件皮肤（本质是apk，但无需安装）。

**injor** 的灵感来自<a href="https://github.com/hongyangAndroid/AndroidChangeSkin">AndroidChangeSkin</a>，原理一样，均是在换肤时遍历contentView，但**injor**内置更多的特性，具有更好、更强的扩展性也更简单，并且支持异步换肤 —— 更换皮肤时，对ViewTree的遍历，语法解析，资源加载均在子线程中进行，只有将资源设置到view上时才在主线程中运行。因此对于大量item（成千上万，可在<a href="https://github.com/hack2ware/injor/tree/master/SkinTest">SkinTest</a>中模拟）的界面，换肤时界面仍保持响应（虽然有点卡，但不至于出现ANR，因此可在换肤时显示进度对话框）。


#说明
- <i>/injor</i> 中仅包含框架代码，直接打包<i>/injor/src</i>中的代码即可使用。bin包大概35K，bin_with_src包大概51K。
- <i>/SkinTest</i> 演示了如何使用**injor**，包含对**injor**默认支持的7种resType的简单使用，性能测试以及如何扩展**injor**（演示了如何扩展injor使得可通过皮肤调整界面亮度）。用ADT或Android Studio直接导入均可运行。

#特性
**injor** 默认支持7种resType，分别是：
- background 切换View及其子类的背景
- color 切换TextView及其子类的文字颜色
- text 切换TextView及其子类的文字
- textSize 切换TextView及其子类的文字大小
- divider 切换ListView及其子类的分割器
- typeface 切换TextView及其子类的字体
- src 切换ImageView及其子类的源

在**injor**中，每一个resType均由一个对应的处理器（ISkinProcessor）处理，要想扩展resType，只需实现一个ISkinProcessor并将它注册到**injor**即可，**injor**便能理解皮肤语法中的新resType。**injor**默认开启异步换肤，这对于包含大量item的界面尤为有用，但也可选择关闭它，少量item的界面换肤反而更快。**injor**支持切换应用内置皮肤和插件皮肤，插件皮肤就是一个APK，但无需安装。皮肤资源的组织方式就是Android的那一套，**injor**也是通过android.content.res.Resources去获取资源。这也是为什么说它是“原生”的原因。唯一不同的是，由于**injor**内置字体切换特性，而Android对字体切换不太友好。因此需要把字体放置在<i>/assets/fonts</i>目录下，**injor**通过Typeface.createFromAsset(AssetManager)去获取并自动做字体缓存。**injor**换肤是动态的，不用重启Activity，并自动记录当前的皮肤，换肤后会一直生效。
