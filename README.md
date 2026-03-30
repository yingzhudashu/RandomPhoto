# 📱 随机照片 (Random Photo)

一个简洁实用的 Android 应用，从相册中随机展示照片，带来惊喜的回忆体验。

[![Platform](https://img.shields.io/badge/platform-Android-blue.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.20-purple.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## ✨ 功能特性

- 🎲 **随机展示** - 从相册中随机选择照片
- 🖼️ **全屏查看** - 全屏查看照片细节
- 🔄 **刷新按钮** - 点击刷新随机照片
- ❤️ **收藏功能** - 收藏喜欢的照片
- 📤 **分享照片** - 分享照片到社交媒体
- 🎨 **Material Design** - 简洁美观的界面设计
- 🌙 **深色模式** - 支持系统深色模式

## 📸 应用截图

![主界面](screenshots/main.png)
![全屏查看](screenshots/fullscreen.png)
*(截图待添加)*

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17 或更高版本
- Android SDK 34
- 最低支持 Android 8.0 (API 26)

### 编译步骤

1. **克隆仓库**
   ```bash
   git clone https://github.com/YOUR_USERNAME/RandomPhoto.git
   cd RandomPhoto
   ```

2. **用 Android Studio 打开项目**
   - 启动 Android Studio
   - File → Open → 选择项目根目录

3. **同步 Gradle**
   - 等待 Gradle 自动同步完成

4. **编译项目**
   ```bash
   # 使用 Gradle 命令
   ./gradlew assembleDebug
   
   # 或在 Android Studio 中
   Build → Make Project
   ```

5. **运行应用**
   - 连接 Android 设备或启动模拟器
   - 点击 Run 按钮

### 安装 APK

编译成功后，APK 文件位于：
```
app/build/outputs/apk/debug/app-debug.apk
```

## 📁 项目结构

```
RandomPhoto/
├── app/
│   ├── src/main/
│   │   ├── java/com/randomphoto/app/
│   │   │   ├── MainActivity.kt          # 主活动入口
│   │   │   ├── MainViewModel.kt         # ViewModel
│   │   │   ├── data/
│   │   │   │   └── PhotoRepository.kt   # 照片数据仓库
│   │   │   ├── ui/
│   │   │   │   ├── components/          # UI 组件
│   │   │   │   └── screens/             # 屏幕组件
│   │   │   └── util/                    # 工具类
│   │   ├── res/                         # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── INSTALL.md
├── USER_GUIDE.md
└── LICENSE
```

## 🛠️ 技术栈

- **语言**: Kotlin 1.9.20
- **UI**: Jetpack Compose
- **架构**: MVVM
- **设计**: Material Design 3
- **依赖注入**: Hilt (可选)
- **异步处理**: Kotlin Coroutines + Flow
- **数据存储**: DataStore / Room (可选)

## 📄 文档

- [安装指南](INSTALL.md) - 详细的安装步骤
- [使用指南](USER_GUIDE.md) - 完整的功能说明
- [快速开始](QUICKSTART.md) - 5 分钟上手指南
- [权限说明](PERMISSIONS.md) - 应用权限说明
- [交付报告](DELIVERY_REPORT.md) - 项目交付报告

## 🔒 隐私与安全

- ✅ **无需网络权限** - 所有数据本地存储
- ✅ **无需账户系统** - 开箱即用
- ✅ **照片不上传** - 所有照片保存在设备本地
- ✅ **开源透明** - 代码完全开源，可审查

## 🤝 贡献

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📝 开发规范

### 代码风格

- 遵循 [Kotlin 编码规范](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 Ktlint 进行代码格式化
- 提交前运行 `./gradlew ktlintCheck`

### 提交信息

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
feat: 添加收藏功能
fix: 修复照片加载失败问题
docs: 更新 README 文档
style: 代码格式化
refactor: 重构 ViewModel 逻辑
test: 添加单元测试
chore: 更新依赖版本
```

## 📄 开源协议

本项目采用 MIT 协议开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 👨‍💻 作者

- **Your Name** - [@yourusername](https://github.com/yourusername)

## 🙏 致谢

感谢以下开源项目：

- [Android Jetpack](https://developer.android.com/jetpack)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Material Design](https://material.io/)

## 📞 联系方式

- 问题反馈：[GitHub Issues](https://github.com/YOUR_USERNAME/RandomPhoto/issues)
- 邮箱：your.email@example.com

---

**⭐ 如果喜欢这个项目，请给个 Star 支持一下！**
