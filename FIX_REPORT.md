# 馃敡 Android 鍒犻櫎鐓х墖鏉冮檺淇鎶ュ憡

## 淇鏃ユ湡
2026-03-29

## 闂鎻忚堪
鐢ㄦ埛鍙嶉锛氬湪 RandomPhotoApp 涓偣鍑诲垹闄ゆ寜閽椂锛屾彁绀?鍒犻櫎澶辫触"銆?
**鏍规湰鍘熷洜**锛?- Android 10+ (API 29+) 寮曞叆浜嗗垎鍖哄瓨鍌紙Scoped Storage锛?- 搴旂敤鍙兘鐩存帴鍒犻櫎鑷繁鍒涘缓鐨勭収鐗?- 瀵逛簬鐩稿唽涓殑鍏朵粬鐓х墖锛岄渶瑕佷娇鐢ㄧ郴缁熷垹闄ゆ帴鍙ｅ苟鑾峰彇鐢ㄦ埛纭

---

## 淇鍐呭

### 鉁?姝ラ 1锛氭洿鏂?AndroidManifest.xml

**淇敼鍓?*锛?```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="32"
    tools:ignore="ScopedStorage" />
```

**淇敼鍚?*锛?```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="29"
    tools:ignore="ScopedStorage" />
```

**璇存槑**锛歐RITE_EXTERNAL_STORAGE 鏉冮檺浠呭湪 Android 9 鍙婁互涓嬮渶瑕侊紝Android 10+ 浣跨敤鏂扮殑鏉冮檺妯″瀷銆?
---

### 鉁?姝ラ 2锛氭洿鏂?PhotoRepository.kt

**鏂板鍒犻櫎缁撴灉绫诲瀷**锛?```kotlin
sealed class DeleteResult {
    object Success : DeleteResult()
    data class NeedsConfirmation(val intentSender: IntentSender) : DeleteResult()
    data class Failure(val error: String) : DeleteResult()
}
```

**鏇存柊鍒犻櫎鏂规硶**锛?```kotlin
suspend fun deletePhoto(photo: Photo): DeleteResult {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+锛氬皾璇曠洿鎺ュ垹闄ゆ垨杩斿洖纭璇锋眰
        try {
            val rowsDeleted = contentResolver.delete(photo.uri, null, null)
            if (rowsDeleted > 0) {
                DeleteResult.Success
            } else {
                // 闇€瑕佺敤鎴风‘璁?                val pendingIntent = MediaStore.createDeleteRequest(
                    contentResolver,
                    listOf(photo.uri)
                )
                DeleteResult.NeedsConfirmation(pendingIntent.intentSender)
            }
        } catch (e: RecoverableSecurityException) {
            // 闇€瑕佺敤鎴风‘璁ゆ潈闄?            val pendingIntent = MediaStore.createDeleteRequest(
                contentResolver,
                listOf(photo.uri)
            )
            DeleteResult.NeedsConfirmation(pendingIntent.intentSender)
        }
    } else {
        // Android 9 鍙婁互涓嬶細鐩存帴鍒犻櫎鏂囦欢
        val file = File(photo.uri.path)
        if (file.exists() && file.delete()) {
            DeleteResult.Success
        } else {
            DeleteResult.Failure("鍒犻櫎澶辫触锛岃妫€鏌ユ潈闄?)
        }
    }
}
```

---

### 鉁?姝ラ 3锛氭洿鏂?MainViewModel.kt

**鏂板鍒犻櫎璇锋眰缁撴灉绫诲瀷**锛?```kotlin
sealed class DeleteRequestResult {
    object Success : DeleteRequestResult()
    data class NeedsConfirmation(val intentSender: IntentSender) : DeleteRequestResult()
    data class Failure(val error: String) : DeleteRequestResult()
}
```

**鏇存柊鍒犻櫎鏂规硶**锛?```kotlin
suspend fun deleteCurrentPhoto(): DeleteRequestResult {
    val result = photoRepository.deletePhoto(photoToDelete)
    
    when (result) {
        is PhotoRepository.DeleteResult.Success -> {
            // 鏇存柊 UI 鐘舵€?            allPhotos = allPhotos.filter { it.uri != photoToDelete.uri }
            currentPhoto = null
            DeleteRequestResult.Success
        }
        is PhotoRepository.DeleteResult.NeedsConfirmation -> {
            // 杩斿洖纭璇锋眰缁?Activity
            DeleteRequestResult.NeedsConfirmation(result.intentSender)
        }
        is PhotoRepository.DeleteResult.Failure -> {
            DeleteRequestResult.Failure(result.error)
        }
    }
}

// 鏂板锛氬畬鎴愬垹闄わ紙鐢ㄦ埛纭鍚庤皟鐢級
fun completeDelete() {
    allPhotos = allPhotos.filter { it.uri != currentPhoto?.uri }
    currentPhoto = null
    // 鏇存柊 UI 鐘舵€?}

// 鏂板锛氬彇娑堝垹闄?fun cancelDelete() {
    // 涓嶅仛浠讳綍鎿嶄綔
}
```

---

### 鉁?姝ラ 4锛氭洿鏂?MainActivity.kt

**鏂板鍒犻櫎纭鍚姩鍣?*锛?```kotlin
internal val deleteConfirmLauncher = registerForActivityResult(
    ActivityResultContracts.StartIntentSenderForResult()
) { result ->
    if (result.resultCode == RESULT_OK) {
        viewModel.completeDelete()
        Toast.makeText(this, "鐓х墖宸插垹闄?, Toast.LENGTH_SHORT).show()
    } else {
        viewModel.cancelDelete()
        Toast.makeText(this, "宸插彇娑堝垹闄?, Toast.LENGTH_SHORT).show()
    }
}
```

**鏇存柊 Composable 鍑芥暟绛惧悕**锛?```kotlin
@Composable
fun RandomPhotoApp(
    viewModel: MainViewModel = viewModel(),
    deleteConfirmLauncher: ActivityResultLauncher<IntentSenderRequest>
) {
    // ...
    when (result) {
        is MainViewModel.DeleteRequestResult.NeedsConfirmation -> {
            deleteConfirmLauncher.launch(
                IntentSenderRequest.Builder(result.intentSender).build()
            )
        }
        // ...
    }
}
```

---

## 缂栬瘧楠岃瘉

### 缂栬瘧鍛戒护
```bash
$env:JAVA_HOME="D:\Android\Android Studio\jbr"
cd C:\Users\16785\.openclaw\workspace\RandomPhotoApp
& "C:\Users\16785\.gradle\wrapper\dists\gradle-8.2-bin\bbg7u40eoinfdyxsxr3z4i7ta\gradle-8.2\bin\gradle.bat" assembleDebug
```

### 缂栬瘧缁撴灉
- 鉁?**BUILD SUCCESSFUL**
- 鉁?鏃犵紪璇戦敊璇?- 鉁?APK 鐢熸垚鎴愬姛锛歚app-debug.apk` (8.3MB)
- 鉁?鏉冮檺閰嶇疆姝ｇ‘
- 鉁?鍒犻櫎閫昏緫鍖哄垎 Android 鐗堟湰

---

## 娴嬭瘯璇存槑

### 娴嬭瘯鍦烘櫙 1锛欰ndroid 10+ (API 29+)
1. 鎵撳紑 App锛屾樉绀洪殢鏈虹収鐗?2. 鐐瑰嚮鍒犻櫎鎸夐挳
3. 寮瑰嚭鍒犻櫎纭瀵硅瘽妗?4. 鐐瑰嚮"鍒犻櫎" 鈫?绯荤粺鍒犻櫎鐓х墖 鈫?鏄剧ず"鐓х墖宸插垹闄?鎻愮ず
5. 鑷姩鍔犺浇涓嬩竴寮犻殢鏈虹収鐗?
**棰勬湡缁撴灉**锛?- 鉁?寮瑰嚭绯荤粺纭瀵硅瘽妗?- 鉁?鐢ㄦ埛纭鍚庡垹闄ゆ垚鍔?- 鉁?鑷姩鍔犺浇涓嬩竴寮犵収鐗?
### 娴嬭瘯鍦烘櫙 2锛欰ndroid 9 鍙婁互涓?(API 28-)
1. 鎵撳紑 App锛屾樉绀洪殢鏈虹収鐗?2. 鐐瑰嚮鍒犻櫎鎸夐挳
3. 寮瑰嚭鍒犻櫎纭瀵硅瘽妗?4. 鐐瑰嚮"鍒犻櫎" 鈫?鐩存帴鍒犻櫎鐓х墖 鈫?鏄剧ず"鐓х墖宸插垹闄?鎻愮ず
5. 鑷姩鍔犺浇涓嬩竴寮犻殢鏈虹収鐗?
**棰勬湡缁撴灉**锛?- 鉁?鐩存帴鍒犻櫎鎴愬姛锛堟棤闇€绯荤粺纭锛?- 鉁?鑷姩鍔犺浇涓嬩竴寮犵収鐗?
---

## 娉ㄦ剰浜嬮」

### Android 10+ 闄愬埗
- 搴旂敤鍙兘鐩存帴鍒犻櫎鑷繁鍒涘缓鐨勭収鐗?- 瀵逛簬鐩稿唽涓殑鍏朵粬鐓х墖锛屽繀椤婚€氳繃 `MediaStore.createDeleteRequest()` 鑾峰彇鐢ㄦ埛纭
- 杩欐槸 Android 绯荤粺鐨勫畨鍏ㄦ満鍒讹紝鏃犳硶缁曡繃

### Android 9 鍙婁互涓嬭姹?- 闇€瑕?`WRITE_EXTERNAL_STORAGE` 鏉冮檺
- 宸插湪 AndroidManifest.xml 涓厤缃紙maxSdkVersion="29"锛?- 杩愯鏃朵細鑷姩璇锋眰璇ユ潈闄?
### 鍒嗗尯瀛樺偍锛圫coped Storage锛?- Android 10+ 寮曞叆鐨勫瓨鍌ㄨ闂満鍒?- 搴旂敤鏃犳硶鐩存帴璁块棶鍏朵粬搴旂敤鐨勬枃浠?- 蹇呴』閫氳繃 MediaStore API 鎴?Storage Access Framework

---

## 浜や粯鏂囦欢

- 鉁?`app-debug.apk` - 淇鍚庣殑瀹夎鍖?- 鉁?瀹屾暣婧愪唬鐮佸凡鏇存柊
- 鉁?鏈慨澶嶆姤鍛?
---

## 鍚庣画寤鸿

1. **娴嬭瘯瑕嗙洊**锛氬湪涓嶅悓 Android 鐗堟湰涓婃祴璇曞垹闄ゅ姛鑳?2. **鐢ㄦ埛浣撻獙**锛氳€冭檻娣诲姞鍒犻櫎鎾ら攢鍔熻兘
3. **閿欒澶勭悊**锛氫紭鍖栭敊璇彁绀猴紝鎻愪緵鏇磋缁嗙殑澶辫触鍘熷洜
4. **鏉冮檺璇存槑**锛氬湪棣栨鍚姩鏃跺悜鐢ㄦ埛瑙ｉ噴涓轰粈涔堥渶瑕佸垹闄ゆ潈闄?
---

**淇瀹屾垚锛?* 馃帀
