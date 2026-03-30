# 棣冩懌 闂呭繑婧€閻撗呭 (Random Photo)

娑撯偓娑擃亞鐣濆ú浣哥杽閻劎娈?Android 鎼存梻鏁ら敍灞肩矤閻╃鍞芥稉顓㈡閺堝搫鐫嶇粈铏瑰弾閻楀浄绱濈敮锔芥降閹﹤鏋╅惃鍕礀韫囧棔缍嬫灞烩偓?
[![Platform](https://img.shields.io/badge/platform-Android-blue.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.20-purple.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 閴?閸旂喕鍏橀悧瑙勨偓?
- 棣冨箟 **闂呭繑婧€鐏炴洜銇?* - 娴犲海娴夐崘灞艰厬闂呭繑婧€闁瀚ㄩ悡褏澧?- 棣冩煠閿?**閸忋劌鐫嗛弻銉ф箙** - 閸忋劌鐫嗛弻銉ф箙閻撗呭缂佸棜濡?- 棣冩敡 **閸掗攱鏌婇幐澶愭尦** - 閻愮懓鍤崚閿嬫煀闂呭繑婧€閻撗呭
- 閴傘倧绗?**閺€鎯版閸旂喕鍏?* - 閺€鎯版閸犳粍顐介惃鍕弾閻?- 棣冩憶 **閸掑棔闊╅悡褏澧?* - 閸掑棔闊╅悡褏澧栭崚鎵仦娴溿倕鐛熸担?- 棣冨腹 **Material Design** - 缁犫偓濞蹭胶绶ㄧ憴鍌滄畱閻ｅ矂娼扮拋鎹愵吀
- 棣冨 **濞ｈ精澹婂Ο鈥崇础** - 閺€顖涘瘮缁崵绮哄ǎ杈濡€崇础

## 棣冩懗 鎼存梻鏁ら幋顏勬禈

![娑撹崵鏅棃顣?screenshots/main.png)
![閸忋劌鐫嗛弻銉ф箙](screenshots/fullscreen.png)
*(閹搭亜娴樺鍛潑閸?*

## 棣冩畬 韫囶偊鈧喎绱戞慨?
### 閻滎垰顣ㄧ憰浣圭湴

- Android Studio Hedgehog (2023.1.1) 閹存牗娲挎妯煎閺?- JDK 17 閹存牗娲挎妯煎閺?- Android SDK 34
- 閺堚偓娴ｅ孩鏁幐?Android 8.0 (API 26)

### 缂傛牞鐦у銉╊€?
1. **閸忓娈曟禒鎾崇氨**
   ```bash
   git clone https://github.com/yingzhudashu/RandomPhoto.git
   cd RandomPhoto
   ```

2. **閻?Android Studio 閹垫挸绱戞い鍦窗**
   - 閸氼垰濮?Android Studio
   - File 閳?Open 閳?闁瀚ㄦい鍦窗閺嶅湱娲拌ぐ?
3. **閸氬本顒?Gradle**
   - 缁涘绶?Gradle 閼奉亜濮╅崥灞绢劄鐎瑰本鍨?
4. **缂傛牞鐦фい鍦窗**
   ```bash
   # 娴ｈ法鏁?Gradle 閸涙垝鎶?   ./gradlew assembleDebug
   
   # 閹存牕婀?Android Studio 娑?   Build 閳?Make Project
   ```

5. **鏉╂劘顢戞惔鏃傛暏**
   - 鏉╃偞甯?Android 鐠佹儳顦幋鏍ф儙閸斻劍膩閹风喎娅?   - 閻愮懓鍤?Run 閹稿鎸?
### 鐎瑰顥?APK

缂傛牞鐦ч幋鎰閸氬函绱滱PK 閺傚洣娆㈡担宥勭艾閿?```
app/build/outputs/apk/debug/app-debug.apk
```

## 棣冩惂 妞ゅ湱娲扮紒鎾寸€?
```
RandomPhoto/
閳规壕鏀㈤埞鈧?app/
閳?  閳规壕鏀㈤埞鈧?src/main/
閳?  閳?  閳规壕鏀㈤埞鈧?java/com/randomphoto/app/
閳?  閳?  閳?  閳规壕鏀㈤埞鈧?MainActivity.kt          # 娑撶粯妞块崝銊ュ弳閸?閳?  閳?  閳?  閳规壕鏀㈤埞鈧?MainViewModel.kt         # ViewModel
閳?  閳?  閳?  閳规壕鏀㈤埞鈧?data/
閳?  閳?  閳?  閳?  閳规柡鏀㈤埞鈧?PhotoRepository.kt   # 閻撗呭閺佺増宓佹禒鎾崇氨
閳?  閳?  閳?  閳规壕鏀㈤埞鈧?ui/
閳?  閳?  閳?  閳?  閳规壕鏀㈤埞鈧?components/          # UI 缂佸嫪娆?閳?  閳?  閳?  閳?  閳规柡鏀㈤埞鈧?screens/             # 鐏炲繐绠风紒鍕
閳?  閳?  閳?  閳规柡鏀㈤埞鈧?util/                    # 瀹搞儱鍙跨猾?閳?  閳?  閳规壕鏀㈤埞鈧?res/                         # 鐠у嫭绨弬鍥︽
閳?  閳?  閳规柡鏀㈤埞鈧?AndroidManifest.xml
閳?  閳规柡鏀㈤埞鈧?build.gradle.kts
閳规壕鏀㈤埞鈧?gradle/
閳规壕鏀㈤埞鈧?build.gradle.kts
閳规壕鏀㈤埞鈧?settings.gradle.kts
閳规壕鏀㈤埞鈧?README.md
閳规壕鏀㈤埞鈧?INSTALL.md
閳规壕鏀㈤埞鈧?USER_GUIDE.md
閳规柡鏀㈤埞鈧?LICENSE
```

## 棣冩礈閿?閹垛偓閺堫垱鐖?
- **鐠囶叀鈻?*: Kotlin 1.9.20
- **UI**: Jetpack Compose
- **閺嬭埖鐎?*: MVVM
- **鐠佹崘顓?*: Material Design 3
- **娓氭繆绂嗗▔銊ュ弳**: Hilt (閸欘垶鈧?
- **瀵倹顒炴径鍕倞**: Kotlin Coroutines + Flow
- **閺佺増宓佺€涙ê鍋?*: DataStore / Room (閸欘垶鈧?

## 棣冩惈 閺傚洦銆?
- [鐎瑰顥婇幐鍥у础](INSTALL.md) - 鐠囷妇绮忛惃鍕暔鐟佸懏顒炴?- [娴ｈ法鏁ら幐鍥у础](USER_GUIDE.md) - 鐎瑰本鏆ｉ惃鍕閼冲€燁嚛閺?- [韫囶偊鈧喎绱戞慨濯?QUICKSTART.md) - 5 閸掑棝鎸撴稉濠冨閹稿洤宕?- [閺夊啴妾虹拠瀛樻](PERMISSIONS.md) - 鎼存梻鏁ら弶鍐鐠囧瓨妲?- [娴溿倓绮幎銉ユ啞](DELIVERY_REPORT.md) - 妞ゅ湱娲版禍銈勭帛閹躲儱鎲?
## 棣冩晙 闂呮劗顫嗘稉搴＄暔閸?
- 閴?**閺冪娀娓剁純鎴犵捕閺夊啴妾?* - 閹碘偓閺堝鏆熼幑顔芥拱閸︽澘鐡ㄩ崒?- 閴?**閺冪娀娓剁拹锔藉煕缁崵绮?* - 瀵偓缁犲崬宓嗛悽?- 閴?**閻撗呭娑撳秳绗傛导?* - 閹碘偓閺堝鍙庨悧鍥︾箽鐎涙ê婀拋鎯ь槵閺堫剙婀?- 閴?**瀵偓濠ф劙鈧繑妲?* - 娴狅絿鐖滅€瑰苯鍙忓鈧┃鎰剁礉閸欘垰顓搁弻?
## 棣冾檪 鐠愶紕灏?
濞嗐垼绻嬬拹锛勫盀娴狅絿鐖滈敍浣筋嚞闁潧鎯婃禒銉ょ瑓濮濄儵顎冮敍?
1. Fork 閺堫兛绮ㄦ惔?2. 閸掓稑缂撻悧瑙勨偓褍鍨庨弨?(`git checkout -b feature/AmazingFeature`)
3. 閹绘劒姘﹂弴瀛樻暭 (`git commit -m 'Add some AmazingFeature'`)
4. 閹恒劑鈧礁鍩岄崚鍡樻暜 (`git push origin feature/AmazingFeature`)
5. 瀵偓閸?Pull Request

## 棣冩憫 瀵偓閸欐垼顫夐懠?
### 娴狅絿鐖滄搴㈢壐

- 闁潧鎯?[Kotlin 缂傛牜鐖滅憴鍕瘱](https://kotlinlang.org/docs/coding-conventions.html)
- 娴ｈ法鏁?Ktlint 鏉╂稖顢戞禒锝囩垳閺嶇厧绱￠崠?- 閹绘劒姘﹂崜宥堢箥鐞?`./gradlew ktlintCheck`

### 閹绘劒姘︽穱鈩冧紖

闁潧鎯?[Conventional Commits](https://www.conventionalcommits.org/) 鐟欏嫯瀵栭敍?
```
feat: 濞ｈ濮為弨鎯版閸旂喕鍏?fix: 娣囶喖顦查悡褏澧栭崝鐘烘祰婢惰精瑙﹂梻顕€顣?docs: 閺囧瓨鏌?README 閺傚洦銆?style: 娴狅絿鐖滈弽鐓庣础閸?refactor: 闁插秵鐎?ViewModel 闁槒绶?test: 濞ｈ濮為崡鏇炲帗濞村鐦?chore: 閺囧瓨鏌婃笟婵婄閻楀牊婀?```

## 棣冩惈 瀵偓濠ф劕宕楃拋?
閺堫剟銆嶉惄顕€鍣伴悽?MIT 閸楀繗顔呭鈧┃?- 閺屻儳婀?[LICENSE](LICENSE) 閺傚洣娆㈡禍鍡毿掔拠锔藉剰閵?
## 棣冩噯閳ュ稅鐓?娴ｆ粏鈧?
- **Your Name** - [@yourusername](https://github.com/yourusername)

## 棣冩 閼风闃?
閹扮喕闃挎禒銉ょ瑓瀵偓濠ф劙銆嶉惄顕嗙窗

- [Android Jetpack](https://developer.android.com/jetpack)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Material Design](https://material.io/)

## 棣冩憮 閼辨梻閮撮弬鐟扮础

- 闂傤噣顣介崣宥夘洯閿涙瓟GitHub Issues](https://github.com/yingzhudashu/RandomPhoto/issues)
- 闁喚顔堥敍姝給ur.email@example.com

---

**鐚?婵″倹鐏夐崰婊勵偨鏉╂瑤閲滄い鍦窗閿涘矁顕紒娆庨嚋 Star 閺€顖涘瘮娑撯偓娑撳绱?*

