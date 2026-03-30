# 馃殌 涓婁紶鍒?GitHub 鎸囧崡

## 鍓嶆彁鏉′欢

- 宸插畨瑁?Git
- 宸叉敞鍐?GitHub 璐︽埛
- 宸查厤缃?SSH 瀵嗛挜锛堝彲閫夛紝鎺ㄨ崘锛?
## 鏂规硶 1: 浣跨敤 HTTPS锛堢畝鍗曪級

### 姝ラ 1: 鍦?GitHub 鍒涘缓浠撳簱

1. 璁块棶 https://github.com/new
2. 浠撳簱鍚嶏細RandomPhoto
3. 鍙鎬э細Public锛堝叕寮€锛?4. **涓嶈** 鍒濆鍖?README銆?gitignore 鎴栬鍙瘉锛堟垜浠凡鏈変簡锛?5. 鐐瑰嚮 "Create repository"

### 姝ラ 2: 鍏宠仈杩滅▼浠撳簱

`ash
cd C:\Users\16785\.openclaw\workspace\RandomPhotoApp

# 娣诲姞杩滅▼浠撳簱锛堟浛鎹?YOUR_USERNAME 涓轰綘鐨?GitHub 鐢ㄦ埛鍚嶏級
git remote add origin https://github.com/YOUR_USERNAME/RandomPhoto.git

# 楠岃瘉杩滅▼浠撳簱
git remote -v

# 鎺ㄩ€佸埌 GitHub
git push -u origin master
`

### 姝ラ 3: 杈撳叆 GitHub 鍑嵁

- 鐢ㄦ埛鍚嶏細浣犵殑 GitHub 鐢ㄦ埛鍚?- 瀵嗙爜锛氫娇鐢?**Personal Access Token**锛堜笉鏄处鎴峰瘑鐮侊級

**濡備綍鍒涘缓 Personal Access Token**锛?1. 璁块棶 https://github.com/settings/tokens
2. 鐐瑰嚮 "Generate new token (classic)"
3. 鍕鹃€?epo 鏉冮檺
4. 鐢熸垚骞跺鍒?Token
5. 鍦?git push 鏃剁矘璐?Token锛堜笉浼氭樉绀猴級

---

## 鏂规硶 2: 浣跨敤 SSH锛堟帹鑽愶級

### 姝ラ 1: 鐢熸垚 SSH 瀵嗛挜

`ash
# 鐢熸垚 SSH 瀵嗛挜锛堝鏋滃凡鏈夊彲璺宠繃锛?ssh-keygen -t ed25519 -C "your.email@example.com"

# 鏌ョ湅鍏挜
cat ~/.ssh/id_ed25519.pub
`

### 姝ラ 2: 娣诲姞 SSH 瀵嗛挜鍒?GitHub

1. 澶嶅埗鍏挜鍐呭锛堜粠 id_ed25519.pub 鏂囦欢锛?2. 璁块棶 https://github.com/settings/keys
3. 鐐瑰嚮 "New SSH key"
4. 绮樿创鍏挜骞朵繚瀛?
### 姝ラ 3: 鍏宠仈骞舵帹閫?
`ash
cd C:\Users\16785\.openclaw\workspace\RandomPhotoApp

# 娣诲姞杩滅▼浠撳簱锛堟浛鎹?YOUR_USERNAME锛?git remote add origin git@github.com:YOUR_USERNAME/RandomPhoto.git

# 鎺ㄩ€佸埌 GitHub
git push -u origin master
`

---

## 楠岃瘉鎺ㄩ€?
鎺ㄩ€佹垚鍔熷悗锛?1. 璁块棶 https://github.com/YOUR_USERNAME/RandomPhoto
2. 搴旇鑳界湅鍒版墍鏈夋枃浠跺拰鎻愪氦鍘嗗彶
3. README.md 搴旇姝ｇ‘鏄剧ず

---

## 鍚庣画鏇存柊

`ash
# 鏃ュ父寮€鍙戝悗鎺ㄩ€?git add .
git commit -m "feat: 娣诲姞鏂板姛鑳?
git push origin master
`

---

## 甯歌闂

### Q: 鎺ㄩ€佸け璐?"Permission denied"
**A**: 妫€鏌?SSH 瀵嗛挜鏄惁姝ｇ‘閰嶇疆锛屾垨浣跨敤 HTTPS 鏂瑰紡

### Q: 鎺ㄩ€佸け璐?"Authentication failed"
**A**: 浣跨敤 Personal Access Token 鑰屼笉鏄处鎴峰瘑鐮?
### Q: 濡備綍鏇存崲杩滅▼浠撳簱鍦板潃锛?**A**: 
`ash
git remote set-url origin https://github.com/NEW_USERNAME/RandomPhoto.git
`

### Q: 濡備綍鏌ョ湅鎺ㄩ€佺姸鎬侊紵
**A**: 
`ash
git remote -v
git status
`

---

**绁濋『鍒╀笂浼狅紒** 馃帀
