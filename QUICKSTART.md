# 🚀 快速开始

5 分钟上手随机相册应用！

## 📥 方式一：直接安装（推荐）

### 步骤 1: 下载 APK

前往 [Releases](https://github.com/yingzhudashu/RandomPhoto/releases) 下载最新版本 APK 文件。

### 步骤 2: 安装应用

```
1. 在手机上打开下载的 APK 文件
2. 允许"安装未知应用"权限（如果需要）
3. 点击"安装"
4. 等待安装完成
```

### 步骤 3: 启动应用

```
1. 在手机桌面找到"随机相册"图标
2. 点击启动
3. 授予相册权限
4. 开始使用！
```

**总耗时**: 约 2 分钟 ⏱️

---

## 💻 方式二：自行编译

### 前置要求

- ✅ Android Studio 已安装
- ✅ JDK 17 已配置
- ✅ Git 已安装

### 步骤 1: 克隆仓库

```bash
git clone https://github.com/yingzhudashu/RandomPhoto.git
cd RandomPhoto
```

### 步骤 2: 打开项目

```
1. 启动 Android Studio
2. File → Open
3. 选择 RandomPhoto 文件夹
4. 等待 Gradle 同步完成
```

### 步骤 3: 编译运行

```bash
# 方法 A: 使用 Android Studio
点击 ▶️ Run 按钮

# 方法 B: 使用命令行
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

**总耗时**: 约 5-10 分钟 ⏱️

---

## 🎯 下一步

安装完成后，查看：
- 📖 [使用教程](USER_GUIDE.md) - 学习如何使用
- 🔐 [权限说明](PERMISSIONS.md) - 了解权限用途
- 🛠️ [安装指南](INSTALL.md) - 详细编译步骤

---

**就这么简单！开始享受随机照片带来的惊喜吧！** 🎉
