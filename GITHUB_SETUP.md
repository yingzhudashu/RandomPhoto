# 📦 GitHub 仓库配置指南

## 仓库信息

- **仓库名称**: RandomPhoto
- **仓库地址**: https://github.com/yingzhudashu/RandomPhoto
- **所有者**: yingzhudashu
- **可见性**: 公开 (Public)
- **默认分支**: master

## 🌿 分支管理

### 分支结构

```
main (旧默认分支) → 待删除
└── master (新默认分支) → 当前使用
```

### 删除 main 分支步骤

由于 `main` 是 GitHub 的默认分支，需要先在 GitHub 上更改默认分支：

**步骤 1: 更改默认分支**

1. 打开 https://github.com/yingzhudashu/RandomPhoto/settings/branches
2. 点击 "Default branch" 旁边的 🔄 切换按钮
3. 选择 `master` 作为新的默认分支
4. 点击 "Update" 确认
5. GitHub 会警告此操作不可逆，确认继续

**步骤 2: 删除 main 分支**

在本地执行：
```bash
git fetch origin
git push origin --delete main
```

或在 GitHub 上：
1. 打开 https://github.com/yingzhudashu/RandomPhoto/branches
2. 找到 `main` 分支
3. 点击 🗑️ 删除图标
4. 确认删除

### 分支保护（可选）

为 `master` 分支启用保护：

1. 打开 https://github.com/yingzhudashu/RandomPhoto/settings/branches
2. 点击 "Add rule" 或选择 `master`
3. 启用以下选项：
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
   - ✅ Include administrators

## 🏷️ Release 发布

### 创建 Release

1. 打开 https://github.com/yingzhudashu/RandomPhoto/releases
2. 点击 "Create a new release"
3. 填写信息：
   - **Tag version**: `v1.0.0`
   - **Release title**: `v1.0.0 - Initial Release`
   - **Description**: 更新内容说明
   - ✅ Set as the latest release
4. 上传 APK 文件：
   - 拖拽 `app-debug.apk` 到附件区域
   - 或点击 "Attach binaries" 上传
5. 点击 "Publish release"

### Release 命名规范

```
v{主版本}.{次版本}.{修订号}

示例:
v1.0.0 - 首次发布
v1.1.0 - 添加新功能
v1.0.1 - Bug 修复
```

## 📝 文档要求

### 必需文档

- ✅ README.md - 项目说明（GitHub 首页显示）
- ✅ LICENSE - 开源许可证
- ✅ CONTRIBUTING.md - 贡献指南
- ✅ INSTALL.md - 安装指南

### 推荐文档

- ✅ USER_GUIDE.md - 使用教程
- ✅ QUICKSTART.md - 快速开始
- ✅ PERMISSIONS.md - 权限说明

## 🔧 GitHub Actions（可选）

### 自动编译

创建 `.github/workflows/build.yml`:

```yaml
name: Android Build

on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Grant execute permission
      run: chmod +x gradlew
    
    - name: Build with Gradle
      run: ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

### 自动 Release

创建 `.github/workflows/release.yml`:

```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build Release APK
      run: ./gradlew assembleRelease
    
    - name: Create Release
      uses: softprops/action-gh-release@v1
      with:
        files: app/build/outputs/apk/release/app-release.apk
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

## 📊 仓库统计

### 添加徽章

在 README.md 中添加：

```markdown
[![Stars](https://img.shields.io/github/stars/yingzhudashu/RandomPhoto)](https://github.com/yingzhudashu/RandomPhoto/stargazers)
[![Forks](https://img.shields.io/github/forks/yingzhudashu/RandomPhoto)](https://github.com/yingzhudashu/RandomPhoto/network/members)
[![Issues](https://img.shields.io/github/issues/yingzhudashu/RandomPhoto)](https://github.com/yingzhudashu/RandomPhoto/issues)
[![License](https://img.shields.io/github/license/yingzhudashu/RandomPhoto)](https://github.com/yingzhudashu/RandomPhoto/blob/master/LICENSE)
```

## 🔐 安全设置

### Token 管理

1. 打开 https://github.com/settings/tokens
2. 创建新的 Personal Access Token
3. 选择权限：
   - ✅ repo (完整控制私有仓库)
   - ✅ workflow (管理 GitHub Actions)
4. 复制 Token 并安全保存
5. **不要**将 Token 提交到 Git 仓库

### 密钥配置

在 GitHub 仓库设置中添加 Secrets：

1. 打开 https://github.com/yingzhudashu/RandomPhoto/settings/secrets/actions
2. 点击 "New repository secret"
3. 添加：
   - `KEYSTORE_PATH`: 签名文件路径
   - `KEYSTORE_PASSWORD`: 签名密码
   - `KEY_ALIAS`: 密钥别名
   - `KEY_PASSWORD`: 密钥密码

## 📞 问题排查

### 常见问题

**Q: 无法删除 main 分支？**
A: 先更改默认分支为 master，然后才能删除 main。

**Q: Push 被拒绝？**
A: 检查分支保护设置，或确认有写入权限。

**Q: Actions 不运行？**
A: 检查 `.github/workflows/` 目录下的 YAML 文件语法。

**Q: Release 不显示 APK？**
A: 确认上传成功，检查文件大小限制（50MB）。

---

**配置完成日期**: 2026-03-31  
**配置状态**: ✅ 已完成
