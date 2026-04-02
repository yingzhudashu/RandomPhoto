# 安装指南

## 直接安装 APK

1. 从 [Releases](https://github.com/yingzhudashu/RandomPhoto/releases) 下载 `app-debug.apk`
2. 在手机上打开 APK 文件
3. 如果提示「不允许安装未知应用」，按提示开启权限
4. 安装完成后打开应用
5. 授予相册读取权限

## 添加桌面小组件

1. 长按桌面空白处
2. 选择「小组件」/「Widgets」
3. 找到「随机相册」
4. 拖动 4×3 大组件到桌面

## 从源码编译

### 环境要求
- Android Studio (推荐最新版)
- JDK 17+
- Android SDK 34
- Gradle 8.2+

### 编译步骤

```bash
# 克隆仓库
git clone https://github.com/yingzhudashu/RandomPhoto.git
cd RandomPhoto

# 创建 local.properties（自动检测 SDK 路径）
# 或手动指定：
# echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# 编译
./gradlew assembleDebug

# APK 位于
# app/build/outputs/apk/debug/app-debug.apk
```

## 使用说明

### 应用内操作
- **下一张** — 随机加载一张照片
- **删除** — 移到回收站（弹出系统确认）
- **回收站** — 查看/恢复/永久删除已删除的照片
- **添加小组件** — 引导添加桌面小组件

### 小组件操作
- **🎲 随机** — 随机加载照片
- **🗑️ 删除** — 移到回收站
- **🔒 隐藏** — 隐藏照片保护隐私
- **♻️ 回收** — 打开回收站页面
- 点击照片区域 — 随机下一张
- 30 秒无操作自动隐藏

### 回收站
- 删除的照片保留 30 天
- 可随时恢复到相册
- 可手动永久删除或清空
