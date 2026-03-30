# 贡献指南

感谢你考虑为 RandomPhoto 项目做出贡献！🎉

## 📋 目录

- [行为准则](#行为准则)
- [如何贡献](#如何贡献)
- [开发环境设置](#开发环境设置)
- [提交规范](#提交规范)
- [代码审查](#代码审查)
- [许可证](#许可证)

## 行为准则

本项目采用 [贡献者公约](https://www.contributor-covenant.org/) 行为准则。请尊重所有贡献者和用户。

## 如何贡献

### 报告 Bug

1. 查看 [Issues](https://github.com/YOUR_USERNAME/RandomPhoto/issues) 是否已有相同问题
2. 如果没有，创建新 Issue 并包含：
   - 问题描述
   - 复现步骤
   - 预期行为
   - 实际行为
   - 截图（如适用）
   - 设备信息（Android 版本、设备型号）

### 提出新功能

1. 查看 [Issues](https://github.com/YOUR_USERNAME/RandomPhoto/issues) 是否已有相同建议
2. 创建 Feature Request Issue 并说明：
   - 功能描述
   - 使用场景
   - 预期效果
   - 可能的实现方案

### 提交代码

#### 1. Fork 仓库

点击 GitHub 页面右上角的 "Fork" 按钮

#### 2. 克隆仓库

```bash
git clone https://github.com/YOUR_USERNAME/RandomPhoto.git
cd RandomPhoto
```

#### 3. 创建分支

```bash
git checkout -b feature/amazing-feature
```

分支命名规范：
- `feature/xxx` - 新功能
- `fix/xxx` - Bug 修复
- `docs/xxx` - 文档更新
- `style/xxx` - 代码格式化
- `refactor/xxx` - 代码重构
- `test/xxx` - 测试相关
- `chore/xxx` - 构建/工具相关

#### 4. 进行修改

- 遵循 Kotlin 编码规范
- 添加必要的注释
- 编写单元测试（如适用）
- 更新文档（如适用）

#### 5. 运行检查

```bash
# 代码格式化检查
./gradlew ktlintCheck

# 运行测试
./gradlew test

# 编译检查
./gradlew assembleDebug
```

#### 6. 提交更改

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```bash
git add .
git commit -m "feat: 添加照片收藏功能"
```

提交信息格式：
```
<type>(<scope>): <subject>

<body>

<footer>
```

常用 type：
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式化（不影响代码运行）
- `refactor`: 代码重构（既不是新功能也不是 Bug 修复）
- `test`: 添加或修改测试
- `chore`: 构建过程或辅助工具变动

#### 7. 推送分支

```bash
git push origin feature/amazing-feature
```

#### 8. 创建 Pull Request

1. 在 GitHub 上进入你的 Fork 仓库
2. 点击 "Compare & pull request"
3. 填写 PR 描述：
   - 描述此 PR 的目的
   - 说明测试方法
   - 添加相关 Issue 链接（如 `Fixes #123`）
4. 等待代码审查

## 开发环境设置

### 必需软件

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17 或更高版本
- Android SDK 34
- Git

### 配置步骤

1. **安装 Android Studio**
   - 从 [官网](https://developer.android.com/studio) 下载

2. **安装 JDK 17**
   ```bash
   # macOS
   brew install openjdk@17
   
   # Ubuntu
   sudo apt install openjdk-17-jdk
   ```

3. **配置环境变量**
   ```bash
   export JAVA_HOME=/path/to/jdk-17
   export ANDROID_HOME=/path/to/Android/sdk
   ```

4. **克隆项目**
   ```bash
   git clone https://github.com/YOUR_USERNAME/RandomPhoto.git
   cd RandomPhoto
   ```

5. **打开项目**
   - 启动 Android Studio
   - File → Open → 选择项目根目录

6. **同步 Gradle**
   - 等待 Gradle 自动同步完成

## 代码审查流程

1. **自动检查**
   - CI 会自动运行代码检查和测试
   - 确保所有检查通过

2. **人工审查**
   - 维护者会审查代码质量
   - 可能需要多轮修改

3. **合并**
   - 审查通过后合并到主分支
   - 关闭相关 Issue

## 代码风格

### Kotlin 规范

遵循 [Kotlin 官方编码规范](https://kotlinlang.org/docs/coding-conventions.html)

关键要点：
- 使用 4 个空格缩进
- 类名使用 PascalCase
- 函数和变量使用 camelCase
- 常量使用 UPPER_SNAKE_CASE
- 布尔值使用 is/has/can 前缀

### 示例

```kotlin
// ✅ 好的命名
class PhotoRepository {
    private val isFavorite = false
    private val maxPhotoCount = 100
    
    fun loadPhotos(): List<Photo> {
        // ...
    }
}

// ❌ 不好的命名
class photo_repository {
    private val fav = false
    private val MAX_COUNT = 100
    
    fun getPhotoList(): List<Photo> {
        // ...
    }
}
```

## 测试要求

### 单元测试

```kotlin
class PhotoRepositoryTest {
    
    @Test
    fun `random photo should return non-null photo`() {
        // Given
        val repository = PhotoRepository()
        
        // When
        val photo = repository.getRandomPhoto()
        
        // Then
        assertNotNull(photo)
    }
}
```

### UI 测试

```kotlin
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun mainScreen_displaysRandomPhoto() {
        // 测试主屏幕显示随机照片
    }
}
```

## 文档要求

### 代码注释

```kotlin
/**
 * 从相册中随机选择一张照片
 *
 * @return 随机照片，如果相册为空则返回 null
 * @throws IllegalStateException 当相册未初始化时抛出
 */
fun getRandomPhoto(): Photo? {
    // ...
}
```

### README 更新

如果添加新功能，请更新：
- README.md 的功能列表
- USER_GUIDE.md 的使用说明
- 添加截图（如适用）

## 许可证

通过贡献代码，你同意你的贡献遵循本项目的 [MIT 许可证](LICENSE)。

---

## 🎉 感谢你的贡献！

每一个贡献都让 RandomPhoto 变得更好！🙏

如有任何问题，请随时 [创建 Issue](https://github.com/YOUR_USERNAME/RandomPhoto/issues) 或 [联系维护者](mailto:your.email@example.com)。
