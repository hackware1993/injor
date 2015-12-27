# injor
injor   ——   一个高度可扩展的，非侵入式的，简单的，轻量的，强悍的，"原生"的Android皮肤框架，仅35K，无第三方依赖，完美兼容Android 1.0 ~ 6.0。支持内置皮肤和插件皮肤（本质是apk，但无需安装）。injor的灵感来自<a href="https://github.com/hongyangAndroid/AndroidChangeSkin">AndroidChangeSkin</a>，原理一样，均是在换肤时遍历contentView，但injor内置更多的特性，具有更好、更强的扩展性，并且支持异步换肤 ———— 更换皮肤时，ViewTree的遍历，语法解析，资源加载均在子线程中进行，只有将资源设置到view上时才在主线程中运行。因此对于大量item（成千上万）的界面，换肤时界面仍保持响应（虽然有点卡，但不至于出现ANR）。


#说明
* /injor 中仅包含框架代码。直接打包/injor/src中的代码即可使用。jar包大概35K，带上源码51K。
* /SkinTest演示了如何使用injor，包含对injor默认支持的7种resType的简单使用。性能测试以及如何扩展injor（演示了如何扩展injor使得可通过皮肤调整界面亮度）。用ADT或Android Studio直接导入均可运行。
