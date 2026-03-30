# 🤝 贡献指南

感谢你对随机相册项目的关注！欢迎以任何方式参与贡献。

## 💡 可以贡献的内容

### 1. 功能建议

欢迎提出新功能想法：
- 在 [Issues](https://github.com/yingzhudashu/RandomPhoto/issues) 中创建 Feature Request
- 描述清楚使用场景和期望效果
- 欢迎讨论实现方案

### 2. Bug 报告

发现 Bug 请报告：
- 在 [Issues](https://github.com/yingzhudashu/RandomPhoto/issues) 中创建 Bug Report
- 提供以下信息：
  - 设备型号和 Android 版本
  - 复现步骤
  - 预期行为和实际行为
  - 截图或录屏（如有）

### 3. 代码贡献

欢迎提交 PR 修复 Bug 或实现新功能：

#### 贡献流程

```
1. Fork 本仓库
2. 创建功能分支 (git checkout -b feature/AmazingFeature)
3. 提交更改 (git commit -m 'Add some AmazingFeature')
4. 推送到分支 (git push origin feature/AmazingFeature)
5. 创建 Pull Request
```

#### 代码规范

**Kotlin 代码**:
- 遵循 [Kotlin 编码规范](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 4 个空格缩进
- 函数和类添加 KDoc 注释
- 保持代码简洁易读

**提交信息**:
- 使用现在时态 ("Add feature" 而非 "Added feature")
- 首行限制在 72 字符以内
- 必要时添加详细描述

### 4. 文档改进

文档同样重要：
- 修正错别字或语法错误
- 补充缺失的说明
- 优化文档结构
- 添加使用技巧

### 5. 翻译

帮助翻译成其他语言：
- 创建 `i18n` 目录
- 添加对应语言的翻译文件
- 在 README 中更新语言列表

## 🛠️ 开发环境搭建

### 1. 克隆项目

```bash
git clone https://github.com/yingzhudashu/RandomPhoto.git
cd RandomPhoto
```

### 2. 导入 Android Studio

```
File → Open → 选择项目目录
等待 Gradle 同步完成
```

### 3. 运行测试

```bash
# 运行单元测试
./gradlew test

# 运行仪器测试
./gradlew connectedAndroidTest
```

### 4. 代码检查

```bash
# 代码格式化
./gradlew ktlintFormat

# 代码检查
./gradlew ktlintCheck
```

## 📐 架构说明

### 技术栈

- **语言**: Kotlin 1.9.20
- **UI**: Jetpack Compose
- **架构**: MVVM
- **依赖注入**: 无（保持简单）

### 项目结构

```
app/src/main/java/com/randomphoto/app/
├── MainActivity.kt          # 主界面
├── MainViewModel.kt         # 视图模型
├── ui/
│   ├── theme/              # 主题样式
│   └── components/         # UI 组件
└── data/
    └── PhotoRepository.kt   # 照片数据仓库
```

### 核心逻辑

```kotlin
// 1. PhotoRepository 负责加载照片
val photos = PhotoRepository.loadPhotos(context)

// 2. MainViewModel 管理状态
val currentPhoto = viewModel.currentPhoto

// 3. UI 展示照片
Image(painter = rememberImagePainter(currentPhoto))
```

## 🧪 测试要求

提交 PR 前请确保：

- ✅ 所有测试通过
- ✅ 代码格式化
- ✅ 无 lint 警告
- ✅ 新功能有测试覆盖

## 📝 发布流程

1. 更新 `build.gradle.kts` 中的版本号
2. 更新 `CHANGELOG.md`
3. 创建 Git Tag
4. 编译 Release APK
5. 在 GitHub 创建 Release

## 💬 讨论交流

- **Issues**: 功能建议和 Bug 报告
- **Discussions**: 一般性问题讨论
- **Email**: 私下交流（见 GitHub Profile）

## 🙏 致谢

感谢所有贡献者！

---

**你的参与让这个项目变得更好！** ✨
