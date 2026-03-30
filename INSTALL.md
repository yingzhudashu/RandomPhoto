# 馃摜 瀹夎鎸囧崡

鏈寚鍗楀皢甯姪鎮ㄧ紪璇戝拰瀹夎闅忔満鐩稿唽搴旂敤鍒?Android 璁惧銆?
## 馃搵 鍓嶇疆瑕佹眰

### 蹇呴渶杞欢

1. **Android Studio** (鎺ㄨ崘)
   - 鐗堟湰锛欰rctic Fox (2020.3.1) 鎴栨洿楂?   - 涓嬭浇鍦板潃锛歨ttps://developer.android.com/studio

2. **JDK 17**
   - Android Studio 閫氬父鑷甫
   - 鎴栧崟鐙畨瑁咃細https://adoptium.net/

3. **Android SDK**
   - SDK Platform 34 (Android 14)
   - Build-Tools 33.0.2
   - 閫氳繃 Android Studio 鐨?SDK Manager 瀹夎

### 鍙€夊伐鍏?
- **Git** - 鐗堟湰鎺у埗
- **ADB** - Android 璋冭瘯妗ワ紙闅?Android Studio 瀹夎锛?
## 馃敡 缂栬瘧姝ラ

### 鏂规硶 1: 浣跨敤 Android Studio锛堟渶绠€鍗曪級

#### 姝ラ 1: 鎵撳紑椤圭洰

1. 鍚姩 Android Studio
2. 鐐瑰嚮 `File` 鈫?`Open`
3. 閫夋嫨 `RandomPhotoApp` 鏂囦欢澶?4. 鐐瑰嚮 `OK`

#### 姝ラ 2: 绛夊緟 Gradle 鍚屾

- Android Studio 浼氳嚜鍔ㄤ笅杞戒緷璧栧苟鍚屾椤圭洰
- 棣栨鍚屾鍙兘闇€瑕佸嚑鍒嗛挓锛堝彇鍐充簬缃戠粶閫熷害锛?- 鏌ョ湅搴曢儴鐘舵€佹爮锛岀瓑寰?"Gradle sync finished"

#### 姝ラ 3: 杩炴帴璁惧鎴栧惎鍔ㄦā鎷熷櫒

**閫夐」 A: 浣跨敤鐪熸満**
```bash
# 1. 鍚敤寮€鍙戣€呴€夐」
璁剧疆 鈫?鍏充簬鎵嬫満 鈫?杩炵画鐐瑰嚮"鐗堟湰鍙?7 娆?
# 2. 鍚敤 USB 璋冭瘯
璁剧疆 鈫?寮€鍙戣€呴€夐」 鈫?USB 璋冭瘯 鈫?寮€鍚?
# 3. 杩炴帴鐢佃剳
浣跨敤 USB 绾胯繛鎺ユ墜鏈哄拰鐢佃剳

# 4. 楠岃瘉杩炴帴
adb devices
# 搴旇鐪嬪埌璁惧搴忓垪鍙?```

**閫夐」 B: 浣跨敤妯℃嫙鍣?*
```
1. Android Studio 鈫?Tools 鈫?Device Manager
2. 鐐瑰嚮 "Create Device"
3. 閫夋嫨璁惧鍨嬪彿锛堟帹鑽?Pixel 6锛?4. 閫夋嫨绯荤粺闀滃儚锛堟帹鑽?API 34锛?5. 鐐瑰嚮 "Finish"
6. 鐐瑰嚮 鈻讹笍 鍚姩妯℃嫙鍣?```

#### 姝ラ 4: 杩愯搴旂敤

```
鐐瑰嚮宸ュ叿鏍忕殑 鈻讹笍 Run 鎸夐挳
鎴栦娇鐢ㄥ揩鎹烽敭锛歋hift + F10
```

搴旂敤灏嗚嚜鍔ㄧ紪璇戝苟瀹夎鍒拌澶?妯℃嫙鍣ㄣ€?
### 鏂规硶 2: 浣跨敤鍛戒护琛?
#### 姝ラ 1: 璁剧疆鐜鍙橀噺

**Windows (PowerShell):**
```powershell
$env:ANDROID_HOME = "C:\Users\浣犵殑鐢ㄦ埛鍚峔AppData\Local\Android\Sdk"
$env:PATH = "$env:ANDROID_HOME\platform-tools;$env:PATH"
```

**macOS/Linux (Bash):**
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

#### 姝ラ 2: 缂栬瘧 Debug 鐗堟湰

```bash
cd RandomPhotoApp

# Windows
gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

缂栬瘧瀹屾垚鍚庯紝APK 鏂囦欢浣嶄簬锛?```
app/build/outputs/apk/debug/app-debug.apk
```

#### 姝ラ 3: 瀹夎鍒拌澶?
```bash
# 纭繚璁惧宸茶繛鎺?adb devices

# 瀹夎 APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### 姝ラ 4: 鍚姩搴旂敤

```bash
# 鍚姩搴旂敤
adb shell am start -n com.randomphoto.app/.MainActivity
```

## 馃摫 鐢熸垚 Release 鐗堟湰锛堝彲閫夛級

### 姝ラ 1: 鍒涘缓绛惧悕瀵嗛挜

```bash
# 鐢熸垚瀵嗛挜搴?keytool -genkey -v -keystore randomphoto.keystore \
  -alias randomphoto -keyalg RSA -keysize 2048 \
  -validity 10000
```

### 姝ラ 2: 閰嶇疆绛惧悕

缂栬緫 `app/build.gradle.kts`锛屽湪 `android` 鍧椾腑娣诲姞锛?
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("../randomphoto.keystore")
        storePassword = "浣犵殑瀵嗙爜"
        keyAlias = "randomphoto"
        keyPassword = "浣犵殑瀵嗙爜"
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

### 姝ラ 3: 缂栬瘧 Release 鐗堟湰

```bash
./gradlew assembleRelease
```

APK 鏂囦欢浣嶄簬锛?```
app/build/outputs/apk/release/app-release.apk
```

## 馃悰 鏁呴殰鎺掗櫎

### 闂 1: Gradle 鍚屾澶辫触

**閿欒**: `Could not resolve all files for configuration ':app:debugCompileClasspath'`

**瑙ｅ喅鏂规**:
```bash
# 娓呯悊骞堕噸鏂版瀯寤?./gradlew clean
./gradlew build --refresh-dependencies
```

### 闂 2: SDK 鏈壘鍒?
**閿欒**: `SDK location not found`

**瑙ｅ喅鏂规**:
1. 鍦?Android Studio 涓細`File` 鈫?`Project Structure` 鈫?`SDK Location`
2. 鎸囧畾姝ｇ‘鐨?Android SDK 璺緞

### 闂 3: 璁惧鏈瘑鍒?
**閿欒**: `no devices/emulators found`

**瑙ｅ喅鏂规**:
```bash
# 閲嶅惎 ADB 鏈嶅姟鍣?adb kill-server
adb start-server

# 閲嶆柊杩炴帴璁惧
adb devices
```

### 闂 4: 鏉冮檺涓嶈冻锛圠inux/macOS锛?
**閿欒**: `Permission denied`

**瑙ｅ喅鏂规**:
```bash
# 缁?gradlew 娣诲姞鎵ц鏉冮檺
chmod +x gradlew

# 鎴栦娇鐢?sudo
sudo ./gradlew assembleDebug
```

### 闂 5: 缂栬瘧鍐呭瓨涓嶈冻

**閿欒**: `OutOfMemoryError`

**瑙ｅ喅鏂规**:
缂栬緫 `gradle.properties`锛屽鍔?JVM 鍐呭瓨锛?```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

## 馃搳 缂栬瘧鏃堕棿鍙傝€?
| 璁惧 | 棣栨缂栬瘧 | 澧為噺缂栬瘧 |
|------|---------|---------|
| 楂樻€ц兘 PC (i7/32GB) | ~2 鍒嗛挓 | ~15 绉?|
| 涓瓑 PC (i5/16GB) | ~4 鍒嗛挓 | ~30 绉?|
| 浣庨厤 PC (i3/8GB) | ~8 鍒嗛挓 | ~1 鍒嗛挓 |

## 鉁?楠岃瘉瀹夎

瀹夎瀹屾垚鍚庯紝楠岃瘉搴旂敤鏄惁姝ｅ父杩愯锛?
1. 鍦ㄨ澶囦笂鎵惧埌"闅忔満鐩稿唽"鍥炬爣
2. 鐐瑰嚮鍚姩搴旂敤
3. 鎺堜簣鐩稿唽鏉冮檺
4. 鏌ョ湅鏄惁鏄剧ず鐓х墖
5. 娴嬭瘯"涓嬩竴寮?鍜?鍒犻櫎"鍔熻兘

濡傛灉鎵€鏈夊姛鑳芥甯革紝鎭枩锛佸畨瑁呮垚鍔燂紒馃帀

## 馃摓 闇€瑕佸府鍔╋紵

濡傛灉閬囧埌闂锛?
1. 鏌ョ湅 Android Studio 鐨?`Build` 鈫?`Analyze Stacktrace`
2. 浣跨敤 Logcat 鏌ョ湅璇︾粏鏃ュ織
3. 妫€鏌ラ」鐩殑 `build.gradle.kts` 閰嶇疆
4. 纭繚鎵€鏈変緷璧栫増鏈吋瀹?
---

**鏈€鍚庢洿鏂?*: 2026-03-29  
**閫傜敤鐗堟湰**: 1.0.0
