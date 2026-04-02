# 随机相册 (RandomPhoto)

随机展示手机相册照片的 Android 应用，支持桌面小组件和自定义回收站。

## ✨ 功能特性

### 应用内
- 🎲 **随机展示** — 一键随机加载相册中的照片
- 🗑️ **自定义回收站** — 删除的照片先进回收站，30 天后自动清理
- ♻️ **照片恢复** — 从回收站恢复误删的照片到相册
- 📌 **桌面小组件引导** — 一键引导添加桌面小组件

### 桌面小组件 (4×3)
- 🎲 **随机** — 随机加载一张照片
- 🗑️ **删除** — 移到回收站（弹出系统确认）
- 🔒 **隐藏** — 隐藏当前照片保护隐私
- ♻️ **回收** — 快速打开回收站页面
- ⏱️ **自动隐藏** — 30 秒无操作自动隐藏照片

### 同步机制
- 小组件和应用内实时同步当前照片
- 一方删除照片后，另一方自动感知并刷新
- 通过 SharedPreferences 版本号机制实现变更通知

## 📱 截图

> 安装后体验

## 🔧 技术栈

| 技术 | 说明 |
|------|------|
| Kotlin | 开发语言 |
| Jetpack Compose | 应用内 UI 框架 |
| Material Design 3 | 设计规范 |
| MVVM | 架构模式 |
| Coil | 图片加载 |
| MediaStore API | 相册访问 |
| AppWidgetProvider | 桌面小组件 |
| RemoteViews | 小组件 UI |

## 📋 系统要求

- Android 8.0 (API 26) 及以上
- 需要相册读取权限

## 🚀 安装

### 直接安装
从 [Releases](https://github.com/yingzhudashu/RandomPhoto/releases) 下载最新 APK。

### 从源码编译
```bash
# 克隆仓库
git clone https://github.com/yingzhudashu/RandomPhoto.git
cd RandomPhoto

# 设置环境变量
export JAVA_HOME="<Android Studio JBR 路径>"
export ANDROID_HOME="<Android SDK 路径>"

# 编译 Debug APK
./gradlew assembleDebug

# APK 输出位置
# app/build/outputs/apk/debug/app-debug.apk
```

## 📁 项目结构

```
app/src/main/
├── java/com/randomphoto/app/
│   ├── MainActivity.kt          # 主界面 + 导航
│   ├── MainViewModel.kt         # 状态管理 + 业务逻辑
│   ├── data/
│   │   ├── PhotoRepository.kt   # 相册数据源 (MediaStore)
│   │   ├── RecycleBinManager.kt # 自定义回收站管理
│   │   └── PhotoSyncManager.kt  # 同步状态管理
│   ├── ui/components/
│   │   ├── PhotoComponents.kt   # 照片显示组件
│   │   ├── ButtonComponents.kt  # 操作按钮组件
│   │   └── RecycleBinScreen.kt  # 回收站页面
│   ├── ui/theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Typography.kt
│   └── widget/
│       ├── RandomPhotoWidgetProvider.kt  # 小组件逻辑
│       └── TrashRequestActivity.kt       # 系统删除确认
├── res/
│   ├── layout/
│   │   └── widget_random_photo_large.xml # 小组件布局
│   ├── xml/
│   │   └── widget_info_random_photo_large.xml
│   └── values/
│       ├── strings.xml
│       ├── colors.xml
│       └── themes.xml
└── AndroidManifest.xml
```

## 🗑️ 回收站工作原理

采用两步删除流程，兼容 Android 10+ Scoped Storage：

1. **复制** — 将照片复制到 app 私有目录 (`filesDir/recycle_bin/`)
2. **系统确认** — 通过 `MediaStore.createDeleteRequest()` 弹出系统确认框
3. **确认删除** — 用户同意后标记回收站记录为已完成
4. **取消删除** — 用户取消后回滚已复制的文件

回收站照片 30 天后自动永久删除。

## 🔄 同步机制

应用内和小组件通过 SharedPreferences 实现同步：

- `sync_version` — 每次变更递增，用于检测外部修改
- `last_action` — 最近操作类型 (delete/restore)
- `current_photo_id` — 当前显示的照片 MediaStore ID

应用 `onResume` 时检查版本号变化，自动刷新。

## 📄 权限说明

| 权限 | 用途 |
|------|------|
| `READ_MEDIA_IMAGES` | Android 13+ 读取相册 |
| `READ_EXTERNAL_STORAGE` | Android 12 及以下读取相册 |
| `ACCESS_MEDIA_LOCATION` | 访问照片位置信息 |
| `POST_NOTIFICATIONS` | 通知权限 |

## 📝 更新日志

### v1.1 (2026-04-02)
- ✨ 新增自定义回收站（30 天自动清理）
- ✨ 新增回收站页面（恢复/永久删除/清空）
- ✨ 小组件新增第四个按钮（回收站入口）
- 🔧 小组件按钮改为等分高度布局，更易点击
- 🔧 删除流程改为两步确认，兼容 Scoped Storage
- 🔄 增强应用与小组件的实时同步机制
- 🔄 支持检测外部删除并自动刷新

### v1.0 (2026-03-30)
- 🎉 初始版本
- 随机展示相册照片
- 桌面小组件 (4×3)
- 删除照片功能
- 隐私保护（自动隐藏）

## 📜 License

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request。
