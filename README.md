# 馃摫 闅忔満鐓х墖 (Random Photo)

涓€涓畝娲佸疄鐢ㄧ殑 Android 搴旂敤锛屼粠鐩稿唽涓殢鏈哄睍绀虹収鐗囷紝甯︽潵鎯婂枩鐨勫洖蹇嗕綋楠屻€?
[![Platform](https://img.shields.io/badge/platform-Android-blue.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.20-purple.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 鉁?鍔熻兘鐗规€?
- 馃幉 **闅忔満灞曠ず** - 浠庣浉鍐屼腑闅忔満閫夋嫨鐓х墖
- 馃柤锔?**鍏ㄥ睆鏌ョ湅** - 鍏ㄥ睆鏌ョ湅鐓х墖缁嗚妭
- 馃攧 **鍒锋柊鎸夐挳** - 鐐瑰嚮鍒锋柊闅忔満鐓х墖
- 鉂わ笍 **鏀惰棌鍔熻兘** - 鏀惰棌鍠滄鐨勭収鐗?- 馃摛 **鍒嗕韩鐓х墖** - 鍒嗕韩鐓х墖鍒扮ぞ浜ゅ獟浣?- 馃帹 **Material Design** - 绠€娲佺編瑙傜殑鐣岄潰璁捐
- 馃寵 **娣辫壊妯″紡** - 鏀寔绯荤粺娣辫壊妯″紡

## 馃摳 搴旂敤鎴浘

![涓荤晫闈(screenshots/main.png)
![鍏ㄥ睆鏌ョ湅](screenshots/fullscreen.png)
*(鎴浘寰呮坊鍔?*

## 馃殌 蹇€熷紑濮?
### 鐜瑕佹眰

- Android Studio Hedgehog (2023.1.1) 鎴栨洿楂樼増鏈?- JDK 17 鎴栨洿楂樼増鏈?- Android SDK 34
- 鏈€浣庢敮鎸?Android 8.0 (API 26)

### 缂栬瘧姝ラ

1. **鍏嬮殕浠撳簱**
   ```bash
   git clone https://github.com/yingzhudashu/RandomPhoto.git
   cd RandomPhoto
   ```

2. **鐢?Android Studio 鎵撳紑椤圭洰**
   - 鍚姩 Android Studio
   - File 鈫?Open 鈫?閫夋嫨椤圭洰鏍圭洰褰?
3. **鍚屾 Gradle**
   - 绛夊緟 Gradle 鑷姩鍚屾瀹屾垚

4. **缂栬瘧椤圭洰**
   ```bash
   # 浣跨敤 Gradle 鍛戒护
   ./gradlew assembleDebug
   
   # 鎴栧湪 Android Studio 涓?   Build 鈫?Make Project
   ```

5. **杩愯搴旂敤**
   - 杩炴帴 Android 璁惧鎴栧惎鍔ㄦā鎷熷櫒
   - 鐐瑰嚮 Run 鎸夐挳

### 瀹夎 APK

缂栬瘧鎴愬姛鍚庯紝APK 鏂囦欢浣嶄簬锛?```
app/build/outputs/apk/debug/app-debug.apk
```

## 馃搧 椤圭洰缁撴瀯

```
RandomPhoto/
鈹溾攢鈹€ app/
鈹?  鈹溾攢鈹€ src/main/
鈹?  鈹?  鈹溾攢鈹€ java/com/randomphoto/app/
鈹?  鈹?  鈹?  鈹溾攢鈹€ MainActivity.kt          # 涓绘椿鍔ㄥ叆鍙?鈹?  鈹?  鈹?  鈹溾攢鈹€ MainViewModel.kt         # ViewModel
鈹?  鈹?  鈹?  鈹溾攢鈹€ data/
鈹?  鈹?  鈹?  鈹?  鈹斺攢鈹€ PhotoRepository.kt   # 鐓х墖鏁版嵁浠撳簱
鈹?  鈹?  鈹?  鈹溾攢鈹€ ui/
鈹?  鈹?  鈹?  鈹?  鈹溾攢鈹€ components/          # UI 缁勪欢
鈹?  鈹?  鈹?  鈹?  鈹斺攢鈹€ screens/             # 灞忓箷缁勪欢
鈹?  鈹?  鈹?  鈹斺攢鈹€ util/                    # 宸ュ叿绫?鈹?  鈹?  鈹溾攢鈹€ res/                         # 璧勬簮鏂囦欢
鈹?  鈹?  鈹斺攢鈹€ AndroidManifest.xml
鈹?  鈹斺攢鈹€ build.gradle.kts
鈹溾攢鈹€ gradle/
鈹溾攢鈹€ build.gradle.kts
鈹溾攢鈹€ settings.gradle.kts
鈹溾攢鈹€ README.md
鈹溾攢鈹€ INSTALL.md
鈹溾攢鈹€ USER_GUIDE.md
鈹斺攢鈹€ LICENSE
```

## 馃洜锔?鎶€鏈爤

- **璇█**: Kotlin 1.9.20
- **UI**: Jetpack Compose
- **鏋舵瀯**: MVVM
- **璁捐**: Material Design 3
- **渚濊禆娉ㄥ叆**: Hilt (鍙€?
- **寮傛澶勭悊**: Kotlin Coroutines + Flow
- **鏁版嵁瀛樺偍**: DataStore / Room (鍙€?

## 馃搫 鏂囨。

- [瀹夎鎸囧崡](INSTALL.md) - 璇︾粏鐨勫畨瑁呮楠?- [浣跨敤鎸囧崡](USER_GUIDE.md) - 瀹屾暣鐨勫姛鑳借鏄?- [蹇€熷紑濮媇(QUICKSTART.md) - 5 鍒嗛挓涓婃墜鎸囧崡
- [鏉冮檺璇存槑](PERMISSIONS.md) - 搴旂敤鏉冮檺璇存槑
- [浜や粯鎶ュ憡](DELIVERY_REPORT.md) - 椤圭洰浜や粯鎶ュ憡

## 馃敀 闅愮涓庡畨鍏?
- 鉁?**鏃犻渶缃戠粶鏉冮檺** - 鎵€鏈夋暟鎹湰鍦板瓨鍌?- 鉁?**鏃犻渶璐︽埛绯荤粺** - 寮€绠卞嵆鐢?- 鉁?**鐓х墖涓嶄笂浼?* - 鎵€鏈夌収鐗囦繚瀛樺湪璁惧鏈湴
- 鉁?**寮€婧愰€忔槑** - 浠ｇ爜瀹屽叏寮€婧愶紝鍙鏌?
## 馃 璐＄尞

娆㈣繋璐＄尞浠ｇ爜锛佽閬靛惊浠ヤ笅姝ラ锛?
1. Fork 鏈粨搴?2. 鍒涘缓鐗规€у垎鏀?(`git checkout -b feature/AmazingFeature`)
3. 鎻愪氦鏇存敼 (`git commit -m 'Add some AmazingFeature'`)
4. 鎺ㄩ€佸埌鍒嗘敮 (`git push origin feature/AmazingFeature`)
5. 寮€鍚?Pull Request

## 馃摑 寮€鍙戣鑼?
### 浠ｇ爜椋庢牸

- 閬靛惊 [Kotlin 缂栫爜瑙勮寖](https://kotlinlang.org/docs/coding-conventions.html)
- 浣跨敤 Ktlint 杩涜浠ｇ爜鏍煎紡鍖?- 鎻愪氦鍓嶈繍琛?`./gradlew ktlintCheck`

### 鎻愪氦淇℃伅

閬靛惊 [Conventional Commits](https://www.conventionalcommits.org/) 瑙勮寖锛?
```
feat: 娣诲姞鏀惰棌鍔熻兘
fix: 淇鐓х墖鍔犺浇澶辫触闂
docs: 鏇存柊 README 鏂囨。
style: 浠ｇ爜鏍煎紡鍖?refactor: 閲嶆瀯 ViewModel 閫昏緫
test: 娣诲姞鍗曞厓娴嬭瘯
chore: 鏇存柊渚濊禆鐗堟湰
```

## 馃搫 寮€婧愬崗璁?
鏈」鐩噰鐢?MIT 鍗忚寮€婧?- 鏌ョ湅 [LICENSE](LICENSE) 鏂囦欢浜嗚В璇︽儏銆?
## 馃懆鈥嶐煉?浣滆€?
- **Your Name** - [@yourusername](https://github.com/yourusername)

## 馃檹 鑷磋阿

鎰熻阿浠ヤ笅寮€婧愰」鐩細

- [Android Jetpack](https://developer.android.com/jetpack)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Material Design](https://material.io/)

## 馃摓 鑱旂郴鏂瑰紡

- 闂鍙嶉锛歔GitHub Issues](https://github.com/yingzhudashu/RandomPhoto/issues)
- 閭锛歽our.email@example.com

---

**猸?濡傛灉鍠滄杩欎釜椤圭洰锛岃缁欎釜 Star 鏀寔涓€涓嬶紒**

