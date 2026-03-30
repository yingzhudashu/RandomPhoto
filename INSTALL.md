# 🛠️ 安装指南

本指南将帮助您编译和安装随机相册应用到 Android 设备。

## 📋 前置要求

### 必需软件

1. **Android Studio** (推荐)
   - 版本：Arctic Fox (2020.3.1) 或更高
   - 下载地址：https://developer.android.com/studio

2. **JDK 17**
   - Android Studio 通常自带
   - 或单独安装：https://adoptium.net/

3. **Android SDK**
   - SDK Platform 34 (Android 14)
   - Build-Tools 33.0.2
   - 通过 Android Studio 的 SDK Manager 安装

### 可选工具
- **Git** - 版本控制
- **ADB** - Android 调试桥（随 Android Studio 安装）

## 🔧 编译步骤

### 方法 1: 使用 Android Studio（最简单）

#### 步骤 1: 打开项目

1. 启动 Android Studio
2. 点击 `File` → `Open`
3. 选择 `RandomPhotoApp` 文件夹
4. 点击 `OK`

#### 步骤 2: 等待 Gradle 同步

- Android Studio 会自动下载依赖并同步项目
- 首次同步可能需要几分钟（取决于网络速度）
- 查看底部状态栏，等待 "Gradle sync finished"

#### 步骤 3: 连接设备或启动模拟器

**选项 A: 使用真机**
```bash
# 1. 启用开发者选项
设置 → 关于手机 → 连续点击"版本号"7 次

# 2. 启用 USB 调试
设置 → 开发者选项 → USB 调试 → 开启

# 3. 连接电脑
使用 USB 线连接手机和电脑

# 4. 验证连接
adb devices
# 应该看到设备序列号
```

**选项 B: 使用模拟器**
```
1. Android Studio → Tools → Device Manager
2. 点击 "Create Device"
3. 选择设备型号（推荐 Pixel 6）
4. 选择系统镜像（推荐 API 34）
5. 点击 "Finish"
6. 点击 ▶️ 启动模拟器
```

#### 步骤 4: 运行应用

```
点击工具栏的 ▶️ Run 按钮
或使用快捷键：Shift + F10
```

应用将自动编译并安装到设备/模拟器。

### 方法 2: 使用命令行

#### 步骤 1: 设置环境变量

**Windows (PowerShell):**
```powershell
$env:ANDROID_HOME = "C:\Users\你的用户名\AppData\Local\Android\Sdk"
$env:PATH = "$env:ANDROID_HOME\platform-tools;$env:PATH"
```

**macOS/Linux (Bash):**
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

#### 步骤 2: 编译 Debug 版本

```bash
cd RandomPhotoApp

# Windows
gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

编译完成后，APK 文件位于：
```
app/build/outputs/apk/debug/app-debug.apk
```

#### 步骤 3: 安装到设备

```bash
# 确保设备已连接
adb devices

# 安装 APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### 步骤 4: 启动应用

```bash
# 启动应用
adb shell am start -n com.randomphoto.app/.MainActivity
```

## 📦 生成 Release 版本（可选）

### 步骤 1: 创建签名密钥

```bash
# 生成密钥库
keytool -genkey -v -keystore randomphoto.keystore \
  -alias randomphoto -keyalg RSA -keysize 2048 \
  -validity 10000
```

### 步骤 2: 配置签名

编辑 `app/build.gradle.kts`，在 `android` 块中添加：
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("../randomphoto.keystore")
        storePassword = "你的密码"
        keyAlias = "randomphoto"
        keyPassword = "你的密码"
    }
}

buildTypes {
    release {
        isMinifyEnabled = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        signingConfig = signingConfigs.getByName("release")
    }
}
```

### 步骤 3: 编译 Release 版本

```bash
./gradlew assembleRelease
```

APK 文件位于：
```
app/build/outputs/apk/release/app-release.apk
```

## 🐛 故障排除

### 问题 1: Gradle 同步失败

**错误**: `Could not resolve all files for configuration ':app:debugCompileClasspath'`

**解决方案**:
```bash
# 清理并重新构建
./gradlew clean
./gradlew build --refresh-dependencies
```

### 问题 2: SDK 未找到

**错误**: `SDK location not found`

**解决方案**:
1. 在 Android Studio 中：`File` → `Project Structure` → `SDK Location`
2. 指定正确的 Android SDK 路径

### 问题 3: 设备未识别

**错误**: `no devices/emulators found`

**解决方案**:
```bash
# 重启 ADB 服务器
adb kill-server
adb start-server

# 重新连接设备
adb devices
```

### 问题 4: 权限不足（Linux/macOS）

**错误**: `Permission denied`

**解决方案**:
```bash
# 给 gradlew 添加执行权限
chmod +x gradlew

# 或使用 sudo
sudo ./gradlew assembleDebug
```

### 问题 5: 编译内存不足

**错误**: `OutOfMemoryError`

**解决方案**:
编辑 `gradle.properties`，增加 JVM 内存：
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

## ⏱️ 编译时间参考

| 设备 | 首次编译 | 增量编译 |
|------|---------|---------|
| 高性能 PC (i7/32GB) | ~2 分钟 | ~15 秒 |
| 中等 PC (i5/16GB) | ~4 分钟 | ~30 秒 |
| 低配 PC (i3/8GB) | ~8 分钟 | ~1 分钟 |

## ✅ 验证安装

安装完成后，验证应用是否正常运行：
1. 在设备上找到"随机相册"图标
2. 点击启动应用
3. 授予相册权限
4. 查看是否显示照片
5. 测试"下一张"和"删除"功能

如果所有功能正常，恭喜！安装成功！🎉

## 📞 需要帮助？

如果遇到问题：
1. 查看 Android Studio 的 `Build` → `Analyze Stacktrace`
2. 使用 Logcat 查看详细日志
3. 检查项目的 `build.gradle.kts` 配置
4. 确保所有依赖版本兼容

---

**最后更新**: 2026-03-31  
**适用版本**: 1.0.0
