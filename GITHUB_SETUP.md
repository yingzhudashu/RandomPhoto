# 🚀 上传到 GitHub 指南

## 前提条件

- 已安装 Git
- 已注册 GitHub 账户
- 已配置 SSH 密钥（可选，推荐）

## 方法 1: 使用 HTTPS（简单）

### 步骤 1: 在 GitHub 创建仓库

1. 访问 https://github.com/new
2. 仓库名：RandomPhoto
3. 可见性：Public（公开）
4. **不要** 初始化 README、.gitignore 或许可证（我们已有了）
5. 点击 "Create repository"

### 步骤 2: 关联远程仓库

`ash
cd C:\Users\16785\.openclaw\workspace\RandomPhotoApp

# 添加远程仓库（替换 YOUR_USERNAME 为你的 GitHub 用户名）
git remote add origin https://github.com/YOUR_USERNAME/RandomPhoto.git

# 验证远程仓库
git remote -v

# 推送到 GitHub
git push -u origin master
`

### 步骤 3: 输入 GitHub 凭据

- 用户名：你的 GitHub 用户名
- 密码：使用 **Personal Access Token**（不是账户密码）

**如何创建 Personal Access Token**：
1. 访问 https://github.com/settings/tokens
2. 点击 "Generate new token (classic)"
3. 勾选 epo 权限
4. 生成并复制 Token
5. 在 git push 时粘贴 Token（不会显示）

---

## 方法 2: 使用 SSH（推荐）

### 步骤 1: 生成 SSH 密钥

`ash
# 生成 SSH 密钥（如果已有可跳过）
ssh-keygen -t ed25519 -C "your.email@example.com"

# 查看公钥
cat ~/.ssh/id_ed25519.pub
`

### 步骤 2: 添加 SSH 密钥到 GitHub

1. 复制公钥内容（从 id_ed25519.pub 文件）
2. 访问 https://github.com/settings/keys
3. 点击 "New SSH key"
4. 粘贴公钥并保存

### 步骤 3: 关联并推送

`ash
cd C:\Users\16785\.openclaw\workspace\RandomPhotoApp

# 添加远程仓库（替换 YOUR_USERNAME）
git remote add origin git@github.com:YOUR_USERNAME/RandomPhoto.git

# 推送到 GitHub
git push -u origin master
`

---

## 验证推送

推送成功后：
1. 访问 https://github.com/YOUR_USERNAME/RandomPhoto
2. 应该能看到所有文件和提交历史
3. README.md 应该正确显示

---

## 后续更新

`ash
# 日常开发后推送
git add .
git commit -m "feat: 添加新功能"
git push origin master
`

---

## 常见问题

### Q: 推送失败 "Permission denied"
**A**: 检查 SSH 密钥是否正确配置，或使用 HTTPS 方式

### Q: 推送失败 "Authentication failed"
**A**: 使用 Personal Access Token 而不是账户密码

### Q: 如何更换远程仓库地址？
**A**: 
`ash
git remote set-url origin https://github.com/NEW_USERNAME/RandomPhoto.git
`

### Q: 如何查看推送状态？
**A**: 
`ash
git remote -v
git status
`

---

**祝顺利上传！** 🎉
