# 馃攼 鏉冮檺璇存槑

鏈枃妗ｈ缁嗚鏄庨殢鏈虹浉鍐屽簲鐢ㄦ墍闇€鐨勬潈闄愬強鍏剁敤閫斻€?
## 馃搵 鏉冮檺鍒楄〃

### 1. 鐩稿唽璇诲彇鏉冮檺

#### Android 13+ (API 33+)
```xml
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

**鐢ㄩ€?*: 璇诲彇璁惧鐩稿唽涓殑鐓х墖锛岀敤浜庨殢鏈哄睍绀恒€?
**淇濇姢绾у埆**: 鍗遍櫓鏉冮檺锛堥渶瑕佺敤鎴锋槑纭巿鏉冿級

**鎺堟潈鏂瑰紡**: 
- 搴旂敤鍚姩鏃跺脊鍑虹郴缁熷璇濇
- 鐢ㄦ埛鍙€夋嫨"鍏佽"鎴?鎷掔粷"
- 鍙湪绯荤粺璁剧疆涓殢鏃舵挙閿€

#### Android 12 鍙婁互涓?(API 32-)
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

**鐢ㄩ€?*: 鍦ㄦ棫鐗堟湰 Android 涓婅鍙栧閮ㄥ瓨鍌ㄤ腑鐨勭収鐗囥€?
**璇存槑**: 
- `maxSdkVersion="32"` 琛ㄧず姝ゆ潈闄愪粎鍦?Android 12 鍙婁互涓嬬敓鏁?- Android 13+ 浼氳嚜鍔ㄥ拷鐣ユ鏉冮檺

---

### 2. 鐩稿唽鍐欏叆/鍒犻櫎鏉冮檺

#### Android 10-12 (API 29-32)
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="32"
    tools:ignore="ScopedStorage" />
```

**鐢ㄩ€?*: 鍒犻櫎鐓х墖鏃堕渶瑕佸啓鍏ユ潈闄愩€?
**璇存槑**:
- 浠呭湪 Android 10-12 闇€瑕?- Android 13+ 浣跨敤鏂扮殑濯掍綋鏉冮檺妯″瀷
- `ScopedStorage` 璀﹀憡宸插拷鐣ワ紝鍥犱负鎴戜滑闇€瑕佸垹闄ゅ姛鑳?
#### Android 13+ (API 33+)
```xml
<uses-permission android:name="android.permission.ACCESS_MEDIA_LOCATION" />
```

**鐢ㄩ€?*: 璁块棶濯掍綋鏂囦欢鐨勪綅缃俊鎭紝鐢ㄤ簬鍒犻櫎鎿嶄綔銆?
**璇存槑**:
- Android 13+ 鐨勫垹闄ゆ搷浣滀笉闇€瑕?WRITE_EXTERNAL_STORAGE
- 浣跨敤 MediaStore API 鐩存帴鍒犻櫎

---

## 馃洝锔?鏉冮檺澶勭悊娴佺▼

### 1. 鏉冮檺妫€鏌?
搴旂敤鍚姩鏃惰嚜鍔ㄦ鏌ユ潈闄愶細

```kotlin
private fun checkAndRequestPermission() {
    val permission = when {
        // Android 13+ 浣跨敤鏂版潈闄?        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            Manifest.permission.READ_MEDIA_IMAGES
        }
        // Android 12 鍙婁互涓嬩娇鐢ㄦ棫鏉冮檺
        else -> Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    when {
        // 宸叉巿鏉?        ContextCompat.checkSelfPermission(this, permission) == 
            PackageManager.PERMISSION_GRANTED -> {
            viewModel.loadPhotos()
        }
        // 鏈巿鏉冿紝璇锋眰鏉冮檺
        else -> {
            requestPermissionLauncher.launch(permission)
        }
    }
}
```

### 2. 鏉冮檺璇锋眰

浣跨敤 Activity Result API 璇锋眰鏉冮檺锛?
```kotlin
private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // 鐢ㄦ埛鎺堟潈锛屽姞杞界収鐗?        viewModel.loadPhotos()
    } else {
        // 鐢ㄦ埛鎷掔粷锛屾樉绀烘彁绀?        viewModel.onPermissionDenied()
    }
}
```

### 3. 鏉冮檺鎷掔粷澶勭悊

濡傛灉鐢ㄦ埛鎷掔粷鏉冮檺锛?
1. 鏄剧ず鍙嬪ソ鐨勬彁绀轰俊鎭?2. 鎻愪緵"鎺堜簣鏉冮檺"鎸夐挳
3. 鐐瑰嚮鍚庨噸鏂拌姹傛潈闄?4. 濡傛灉鐢ㄦ埛閫夋嫨"涓嶅啀璇㈤棶"锛屽紩瀵煎埌绯荤粺璁剧疆

---

## 馃摫 鐢ㄦ埛鎺堟潈姝ラ

### 棣栨鍚姩

```
1. 鐐瑰嚮搴旂敤鍥炬爣
   鈫?2. 绯荤粺寮瑰嚭鏉冮檺瀵硅瘽妗?   "闅忔満鐩稿唽鎯宠璁块棶鎮ㄨ澶囦笂鐨勭収鐗?
   鈫?3. 鐢ㄦ埛閫夋嫨锛?   - 鍏佽 鈫?杩涘叆搴旂敤锛屽姞杞界収鐗?   - 鎷掔粷 鈫?鏄剧ず鏉冮檺璇存槑椤甸潰
```

### 閲嶆柊鎺堟潈锛堟潈闄愯鎷掔粷鍚庯級

```
1. 鐐瑰嚮"鎺堜簣鏉冮檺"鎸夐挳
   鈫?2. 绯荤粺鍐嶆寮瑰嚭鏉冮檺瀵硅瘽妗?   鈫?3. 鐢ㄦ埛閫夋嫨锛?   - 鍏佽 鈫?杩涘叆搴旂敤
   - 鎷掔粷 鈫?缁х画鏄剧ず璇存槑椤甸潰
```

### 鍦ㄧ郴缁熻缃腑鎺堟潈

```
1. 璁剧疆 鈫?搴旂敤 鈫?闅忔満鐩稿唽
   鈫?2. 鏉冮檺
   鈫?3. 鎵惧埌"鐓х墖鍜岃棰?鎴?瀛樺偍"
   鈫?4. 閫夋嫨"鍏佽"
```

---

## 馃攳 鏉冮檺浣跨敤閫忔槑搴?
### 鎴戜滑鏀堕泦浠€涔堬紵

**涓嶆敹闆嗕换浣曟暟鎹紒**

- 鉂?涓嶄笂浼犵収鐗囧埌鏈嶅姟鍣?- 鉂?涓嶆敹闆嗙敤鎴蜂俊鎭?- 鉂?涓嶈窡韪敤鎴疯涓?- 鉂?涓嶅寘鍚箍鍛?SDK
- 鉂?涓嶅寘鍚垎鏋愬伐鍏?
### 鎴戜滑浣跨敤浠€涔堬紵

- 鉁?浠呭湪鏈湴璇诲彇鐓х墖鐢ㄤ簬鏄剧ず
- 鉁?浠呭湪鐢ㄦ埛纭鏃跺垹闄ょ収鐗?- 鉁?鎵€鏈夋暟鎹瓨鍌ㄥ湪璁惧鏈湴
- 鉁?涓嶈仈缃戯紝瀹屽叏绂荤嚎杩愯

---

## 鈿狅笍 鏉冮檺瀹夊叏鎻愮ず

### 缁欑敤鎴风殑寤鸿

1. **鍙粠鍙俊鏉ユ簮瀹夎**
   - Google Play
   - 瀹樻柟娓犻亾
   - 鍙俊鐨?APK 缃戠珯

2. **瀹氭湡妫€鏌ユ潈闄?*
   - 璁剧疆 鈫?搴旂敤 鈫?闅忔満鐩稿唽 鈫?鏉冮檺
   - 纭繚鏉冮檺涓庨鏈熶竴鑷?
3. **璀︽儠寮傚父琛屼负**
   - 搴旂敤涓嶅簲璇ュ湪鍚庡彴璁块棶鐓х墖
   - 涓嶅簲璇ヨ姹備笌鍔熻兘鏃犲叧鐨勬潈闄?   - 濡傝仈绯讳汉銆佷綅缃€侀害鍏嬮绛?
### 寮€鍙戣€呯殑鎵胯

- 鏈€灏忔潈闄愬師鍒欙細鍙姹傚繀瑕佺殑鏉冮檺
- 閫忔槑浣跨敤锛氭槑纭鏄庢潈闄愮敤閫?- 鐢ㄦ埛鎺у埗锛氶殢鏃跺彲浠ユ挙閿€鏉冮檺
- 鏁版嵁瀹夊叏锛氭墍鏈夋暟鎹湰鍦板鐞?
---

## 馃搳 鏉冮檺瀵规瘮琛?
| 鏉冮檺 | Android 13+ | Android 10-12 | Android 9- | 鐢ㄩ€?|
|------|-------------|---------------|------------|------|
| READ_MEDIA_IMAGES | 鉁?闇€瑕?| 鉂?涓嶆敮鎸?| 鉂?涓嶆敮鎸?| 璇诲彇鐓х墖 |
| READ_EXTERNAL_STORAGE | 鉂?蹇界暐 | 鉁?闇€瑕?| 鉁?闇€瑕?| 璇诲彇鐓х墖锛堟棫鐗堬級 |
| WRITE_EXTERNAL_STORAGE | 鉂?涓嶉渶瑕?| 鉁?闇€瑕?| 鉁?闇€瑕?| 鍒犻櫎鐓х墖锛堟棫鐗堬級 |
| ACCESS_MEDIA_LOCATION | 鉁?闇€瑕?| 鉂?涓嶉渶瑕?| 鉂?涓嶉渶瑕?| 濯掍綋璁块棶锛堟柊鐗堬級 |

---

## 馃悰 甯歌闂

### Q: 涓轰粈涔堥渶瑕佺浉鍐屾潈闄愶紵
**A**: 搴旂敤鐨勬牳蹇冨姛鑳芥槸灞曠ず鐩稿唽涓殑鐓х墖锛屾病鏈夋鏉冮檺鏃犳硶璁块棶鐓х墖銆?
### Q: 鎺堟潈鍚庤繕鑳芥挙閿€鍚楋紵
**A**: 鍙互銆傞殢鏃跺湪绯荤粺璁剧疆涓挙閿€鏉冮檺锛屽簲鐢ㄤ細鍋滄璁块棶鐓х墖銆?
### Q: 鏉冮檺浼氭硠闇查殣绉佸悧锛?**A**: 涓嶄細銆傚簲鐢ㄥ畬鍏ㄧ绾胯繍琛岋紝鐓х墖涓嶄細涓婁紶鍒颁换浣曟湇鍔″櫒銆?
### Q: 鎷掔粷鏉冮檺鍚庡簲鐢ㄨ繕鑳界敤鍚楋紵
**A**: 涓嶈兘銆傜浉鍐屾潈闄愭槸鏍稿績鍔熻兘锛屾嫆缁濆悗鏃犳硶浣跨敤搴旂敤銆?
### Q: 鍒犻櫎鐓х墖闇€瑕侀澶栨潈闄愬悧锛?**A**: Android 10+ 闇€瑕佸啓鍏ユ潈闄愶紝Android 13+ 浣跨敤鏂扮殑濯掍綋鏉冮檺妯″瀷銆?
### Q: 涓轰粈涔堜笉鍚?Android 鐗堟湰鏉冮檺涓嶅悓锛?**A**: Google 鍦?Android 13 涓紩鍏ヤ簡鏂扮殑闅愮淇濇姢鏈哄埗锛岀粏鍖栦簡濯掍綋璁块棶鏉冮檺銆?
---

## 馃摓 鑱旂郴鏀寔

濡傛湁鏉冮檺鐩稿叧闂锛?
1. 鏌ョ湅鏈潈闄愯鏄庢枃妗?2. 妫€鏌ュ簲鐢ㄧ殑闅愮鏀跨瓥
3. 鑱旂郴寮€鍙戣€呰幏鍙栧府鍔?
---

**鏂囨。鐗堟湰**: 1.0.0  
**鏈€鍚庢洿鏂?*: 2026-03-29  
**閫傜敤鐗堟湰**: Android 8.0+
