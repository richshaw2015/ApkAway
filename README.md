<img align="right" width="110" src="https://raw.githubusercontent.com/richshaw2015/ApkAway/master/src/logo.png">

# 小卓守护——自动拦截恶意安装包

- ❤️ 小卓启动后会自动拦截恶意安装包，守护长者和儿童不受恶意应用骚扰
- ⚠️ 请确保开启了应用的存储权限、开机启动权限、前台服务权限
- ℹ️️ 由于技术原因，Android 11 及以上版本拦截效果不佳

## FAQ

### ❓️ 小卓是怎么诞生的

家中长者和儿童的手机总是会莫名出现很多恶意应用（通常是垃圾清理类），这些应用每隔十几秒就会弹出一个全屏广告，严重影响了手机的正常使用。

我意识到指责他们是没有用的，作为一名开发者，必须要做点什么，于是小卓守护便诞生了。

### ❓️ 小卓是如何运作的

作为一个非系统级应用，能做的并不多，目前的方案是监控应用的存储目录，一旦发现有恶意 APK 文件生成就立刻拦截，使其安装失败。

不过这个方案在 Android 11 以上效果不佳。

### ❓️ 还有其他办法吗

- 关闭“安装未知应用”权限有一些帮助
- ROOT 手机是最彻底的
- 更换 iPhone 是最简单的

### ❓️ 支持和反馈

如果小卓帮助到了您的家人，或者拦截没有生效，或者有更好的想法，都欢迎在 GitHub 上交流。

## Licenses

小卓守护 is licensed under the GPLv3+.  
The file LICENSE includes the full license text.
For more details, check [the license notes](LICENSE.md).
