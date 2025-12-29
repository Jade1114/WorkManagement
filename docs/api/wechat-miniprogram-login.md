# 微信小程序登录/绑定设计（新版）

## 范围与目标
- 仅学生端小程序接入；教师/管理员继续用 Web 账号密码登录。
- 关闭“首次自动创建学生账号”默认行为，改为“已绑定直接登录，未绑定先绑定账号”。

## 核心流程（登录 → 绑定）
1) 小程序 `wx.login` 拿 `code`，调用 `POST /api/auth/weapp/login`。
2) 后端用 `appid/secret/code` 调微信 `jscode2session` 得到 `openid`：
   - 若 `openid` 已绑定用户：直接返回 `{ token, userId, username, role }`。
   - 若未绑定：返回 `{ needBind: true, bindTicket: "<一次性票据>" }`，不自动建号。
3) 小程序弹出绑定表单（账号/密码），调用 `POST /api/auth/weapp/bind`，携带 `bindTicket` + 账号/密码。
4) 后端校验票据→拿到 `openid`，校验账号/密码与账号状态，绑定 `openid` 后生成 JWT 返回。
5) 前端保存 token（`wx.setStorageSync`），后续请求统一带 `Authorization: Bearer <token>`。

## 接口约定
### 1) `POST /api/auth/weapp/login`
请求：
```json
{ "code": "wx.login 返回的 code" }
```
成功（已绑定）：
```json
{ "code":200,"message":"success","data":{"token":"...","userId":1,"username":"student01","role":"student"} }
```
未绑定：
```json
{ "code":200,"message":"need_bind","data":{"needBind":true,"bindTicket":"<一次性票据>","expireSeconds":300} }
```

### 2) `POST /api/auth/weapp/bind`
请求：
```json
{ "bindTicket": "<一次性票据>", "username": "2024xxxx", "password": "******" }
```
成功：
```json
{ "code":200,"message":"success","data":{"token":"...","userId":1,"username":"2024xxxx","role":"student"} }
```
错误示例：`400, "ticket 无效/已过期"`；`400, "账号或密码错误"`；`400, "该微信已绑定其他账号"`；`400, "账号已禁用"`。

## 临时票据说明
- 作用：证明“刚用 code 换到的 openid”，避免前端篡改或重放 openid。
- 生成方式：后端缓存 openid 或签名生成短时 token，含过期时间；有效期建议 5 分钟，用一次即废。

## 后端实现要点
- 保留 `user.openid` 唯一约束；绑定前需检查是否已占用。
- 登录接口分支：已绑定 → 直接登录；未绑定 → 返回 needBind。
- 绑定接口：校验票据→取 openid→校验账号/密码→检查 active 状态→写入 openid→生成 JWT。
- 可配置“允许自动创建学生账号”开关，默认关闭。
- 配置项：`wechat.appid`、`wechat.secret` 放在 yml，不硬编码；必须使用 HTTPS 域名（小程序要求）。

## 小程序交互建议
- 登录态检查：无 token 时先 `wx.login` 走上述流程；绑定失败给出明确提示。
- 票据失效提示：`bindTicket` 过期时，引导重新点击登录获取新票据。
- BASE_URL：正式环境改为备案 HTTPS 域名，并在微信后台配置合法域名。

## 兼容与后续
- 学生业务接口可继续复用现有 `/api/assignments/*`、`/api/submissions/*` 等，Header 携带 token 即可。
- 若未来需要精简返回，可增设 `/api/mobile/v1/*`；教师端小程序可另行规划登录/权限策略。
