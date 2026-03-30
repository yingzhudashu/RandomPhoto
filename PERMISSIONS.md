# 🔐 权限说明

本文档详细说明随机相册应用所需的权限及其用途。

## 📋 权限列表

### 1. 相册读取权限

#### Android 13+ (API 33+)
```xml
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

**用途**: 读取设备相册中的照片，用于随机展示。

**保护级别**: 危险权限（需要用户明确授权）

**授权方式**: 
- 应用启动时弹出系统对话框
- 用户可选择"允许"或"拒绝"
- 可在系统设置中随时撤销

#### Android 12 及以下 (API 32-)
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

**用途**: 在旧版本 Android 上读取外部存储中的照片。

**说明**: 
- `maxSdkVersion="32"` 表示此权限仅在 Android 12 及以下生效
- Android 13+ 会自动忽略此权限

---

### 2. 相册写入/删除权限

#### Android 10-12 (API 29-32)
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="32"
    tools:ignore="ScopedStorage" />
```

**用途**: 删除照片时需要写入权限。

**说明**:
- 仅在 Android 10-12 需要
- Android 13+ 使用新的媒体权限模型
- `ScopedStorage` 警告已忽略，因为我们需要删除功能

#### Android 13+ (API 33+)
```xml
<uses-permission android:name="android.permission.ACCESS_MEDIA_LOCATION" />
```

**用途**: 访问媒体文件的位置信息，用于删除操作。

**说明**:
- Android 13+ 的删除操作不需要 WRITE_EXTERNAL_STORAGE
- 使用 MediaStore API 直接删除

---

## 🛡️ 权限处理流程

### 1. 权限检查

应用启动时自动检查权限：

```kotlin
private fun checkAndRequestPermission() {
    val permission = when {
        // Android 13+ 使用新权限
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            Manifest.permission.READ_MEDIA_IMAGES
        }
        // Android 12 及以下使用旧权限
        else -> Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    when {
        // 已授权
        ContextCompat.checkSelfPermission(this, permission) == 
            PackageManager.PERMISSION_GRANTED -> {
            viewModel.loadPhotos()
        }
        // 未授权，请求权限
        else -> {
            requestPermissionLauncher.launch(permission)
        }
    }
}
```

### 2. 权限请求

使用 Activity Result API 请求权限：

```kotlin
private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // 用户授权，加载照片
        viewModel.loadPhotos()
    } else {
        // 用户拒绝，显示提示
        viewModel.onPermissionDenied()
    }
}
```

### 3. 权限拒绝处理

如果用户拒绝权限：

1. 显示友好的提示信息
2. 提供"授予权限"按钮
3. 点击后重新请求权限
4. 如果用户选择"不再询问"，引导到系统设置

---

## 📱 用户授权步骤

### 首次启动

```
1. 点击应用图标
   ↓
2. 系统弹出权限对话框
   "随机相册想要访问您设备上的照片"
   ↓
3. 用户选择：
   - 允许 → 进入应用，加载照片
   - 拒绝 → 显示权限说明页面
```

### 重新授权（权限被拒绝后）

```
1. 点击"授予权限"按钮
   ↓
2. 系统再次弹出权限对话框
   ↓
3. 用户选择：
   - 允许 → 进入应用
   - 拒绝 → 继续显示说明页面
```

### 在系统设置中授权

```
1. 设置 → 应用 → 随机相册
   ↓
2. 权限
   ↓
3. 找到"照片和视频"或"存储"
   ↓
4. 选择"允许"
```

---

## 🔍 权限使用透明度

### 我们收集什么？

**不收集任何数据！**

- ❌ 不上传照片到服务器
- ❌ 不收集用户信息
- ❌ 不跟踪用户行为
- ❌ 不包含广告 SDK
- ❌ 不包含分析工具

### 我们使用什么？

- ✅ 仅在本地读取照片用于显示
- ✅ 仅在用户确认时删除照片
- ✅ 所有数据存储在设备本地
- ✅ 不联网，完全离线运行

---

## ⚠️ 权限安全提示

### 给用户的建议

1. **只从可信来源安装**
   - Google Play
   - 官方渠道
   - 可信的 APK 网站

2. **定期检查权限**
   - 设置 → 应用 → 随机相册 → 权限
   - 确保权限与预期一致

3. **警惕异常行为**
   - 应用不应该在后台访问照片
   - 不应该请求与功能无关的权限
   - 如联系人、位置、麦克风等

### 开发者的承诺

- 最小权限原则：只请求必要的权限
- 透明使用：明确说明权限用途
- 用户控制：随时可以撤销权限
- 数据安全：所有数据本地处理

---

## 📊 权限对比表

| 权限 | Android 13+ | Android 10-12 | Android 9- | 用途 |
|------|-------------|---------------|------------|------|
| READ_MEDIA_IMAGES | ✅ 需要 | ❌ 不支持 | ❌ 不支持 | 读取照片 |
| READ_EXTERNAL_STORAGE | ❌ 忽略 | ✅ 需要 | ✅ 需要 | 读取照片（旧版） |
| WRITE_EXTERNAL_STORAGE | ❌ 不需要 | ✅ 需要 | ✅ 需要 | 删除照片（旧版） |
| ACCESS_MEDIA_LOCATION | ✅ 需要 | ❌ 不需要 | ❌ 不需要 | 媒体访问（新版） |

---

## 🐛 常见问题

### Q: 为什么需要相册权限？
**A**: 应用的核心功能是展示相册中的照片，没有此权限无法访问照片。

### Q: 授权后还能撤销吗？
**A**: 可以。随时在系统设置中撤销权限，应用会停止访问照片。

### Q: 权限会泄露隐私吗？
**A**: 不会。应用完全离线运行，照片不会上传到任何服务器。

### Q: 拒绝权限后应用还能用吗？
**A**: 不能。相册权限是核心功能，拒绝后无法使用应用。

### Q: 删除照片需要额外权限吗？
**A**: Android 10+ 需要写入权限，Android 13+ 使用新的媒体权限模型。

### Q: 为什么不同 Android 版本权限不同？
**A**: Google 在 Android 13 中引入了新的隐私保护机制，细化了媒体访问权限。

---

## 📞 联系支持

如有权限相关问题：

1. 查看本权限说明文档
2. 检查应用的隐私政策
3. 联系开发者获取帮助

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-29  
**适用版本**: Android 8.0+
