# 微信小程序登录接入指南（对现有 Spring Boot 后端）

## 最终接入方案（当前阶段）
- 范围：仅学生端小程序；教师/管理员继续使用 Web 端账号密码登录。
- 登录：增加 `POST /api/auth/weapp/login`（code→openid→JWT），其他业务接口沿用现有学生接口。
- 接口复用：暂不新增 `/api/mobile/v1`，直接调用现有学生接口即可；若后续需精简返回再扩展。
- 演示：课堂演示使用单个学生账号即可，不切换微信号。

## 核心流程（通用）
1) 小程序端调用 `wx.login()` 获得 `code`。  
2) 后端携带 `appid/secret/code` 请求微信接口 `https://api.weixin.qq.com/sns/jscode2session`，换取 `openid`、`session_key`。  
3) 后端用 `openid` 查库：  
   - 已绑定用户：直接生成业务登录态（JWT）返回。  
   - 未绑定：创建/绑定用户（本项目一般注册为 `student`），存 `openid`，返回业务登录态。  
4) 小程序端拿到后端返回的 JWT 保存（`wx.setStorage`），后续请求在 header `Authorization: Bearer <token>` 携带。  
5) 业务接口按 JWT 鉴权；token 过期重新走 `wx.login`。

> 小程序不能用传统账号密码“静默登录”，必须先用 `code` 换 `openid` 再生成自己的登录态。

## 后端实现要点（结合本项目）
- **新增字段**（用于微信绑定）：`openid`（唯一）。  
- **新接口**：`POST /api/auth/weapp/login`，请求体 `{ code }`，返回 `{ token, userId, role }`。  
- **鉴权复用**：沿用现有 JWT 拦截器；小程序请求同样带 `Authorization`。  
- **绑定策略**：默认自动注册为 `student`；如需学号校验，可额外上送 `studentNo`。  
- **密钥配置**：`appid/secret` 放到配置文件（如 `application-weapp.yml`），避免硬编码。  
- **缓存**：`session_key` 可按需缓存（用于解密手机号/敏感信息）；JWT 过期时间与 Web 端保持一致或适当延长。

## 接口契约示例
```
POST /api/auth/weapp/login
Content-Type: application/json
{
  "code": "<wx.login 返回的 code>"
}

响应 200:
{
  "code": 200,
  "message": "success",
  "data": { "token": "...", "userId": 12, "role": "student" }
}

错误:
- code=400, message="code 无效或已过期"
- code=400, message="账号已禁用"
```

## 后端实现要点（待落地规划）
- 入口（拟定）：`POST /api/auth/weapp/login`。
- 服务：调用 `https://api.weixin.qq.com/sns/jscode2session`，按 `openid` 绑定/注册 `student`，生成 JWT 返回。
- 配置：`application-*.yml` 中预留 `wechat.appid`、`wechat.secret`。
- 模型：`user` 表新增 `openid` 字段用于绑定（需迁移脚本）。

## API 设计草案（征求意见）
> 阶段目标：仅支持「学生端」微信小程序；老师/管理员暂时保持 Web 端账号密码登录，后续再扩展。

### 1) 微信登录/绑定 `POST /api/auth/weapp/login`
- 用途：学生小程序登录并生成业务 JWT（自动注册学生或绑定已有学生账号）。
- 请求体：
```json
{
  "code": "wx.login 返回的 code"
}
```
- 成功响应：
```json
{
  "code": 200,
  "message": "success",
  "data": { "token": "...", "userId": 12, "username": "wx_xxx", "role": "student" }
}
```
- 失败示例：`400, "code 无效或已过期"`；`400, "账号已禁用"`；`500, "微信登录失败: <errmsg>"`。
- 绑定策略（学生）：
  - 按 `openid` 查找；如不存在则注册学生账号（username 生成 `wx_xxxx`，密码占位）。
  - 可选：允许传入 `studentNo` 做一次性绑定校验（若需要，请确认）。

### 2) 学生端接口（当前决定：复用现有接口）
- `GET /api/assignments/pending`（未提交列表）
- `GET /api/assignments/list?courseId=...`（课程作业）
- `POST /api/submissions/create`，请求 `{ assignmentId, content }`
- `GET /api/submissions/my`（我的提交）
- 若未来需要精简返回/改字段，再考虑新增 `/api/mobile/v1/*`。

### 3) 教师/管理员（未来规划）
- 现阶段保持 Web 端账号密码登录与管理界面。
- 如需小程序教师端，后续可新增 `/api/weapp/teacher/login` 与教师版简化接口，或沿用 Web 接口并在路由权限上区分。

### 4) 配置与安全
- `application-*.yml` 增加 `wechat.appid`、`wechat.secret`。
- 所有接口要求 HTTPS；JWT 继续用 `Authorization: Bearer <token>`。
- 可选：新增频控（如登录限流）、请求日志，用于审计。

### 5) 表结构调整（拟新增字段）
- `user.openid` (unique, nullable)
- 若确认采用绑定/注册策略，将提供 SQL 迁移语句。

## 小程序端调用模板
```js
// 登录
wx.login({
  success: async ({ code }) => {
    const res = await wx.request({
      url: BASE_URL + '/api/auth/weapp/login',
      method: 'POST',
      data: { code },
    });
    if (res.data.code === 200) {
      const { token, role } = res.data.data;
      wx.setStorageSync('token', token);
      // 后续请求统一在 header 带 Authorization
    } else {
      wx.showToast({ title: res.data.message, icon: 'none' });
    }
  }
});
```

## 安全与风险控制
- 所有接口使用 HTTPS；后端校验 `code` 只能用一次，过期报错。  
- 限制 `openid` 绑定次数，防止撞库；日志记录登录 IP/UA。  
- 重要接口（提交、批改）建议增加频控与幂等。  
- 上传文件走对象存储 + 后端签名，避免直传后端导致大流量占用。  
- 订阅消息需获取用户授权（`wx.requestSubscribeMessage`），后端用模板触达（作业发布/截止/批改完成）。

## 与现有前后端协作建议
- **接口分组**：可新增 `/api/mobile/v1/*` 聚合简化返回字段，避免直接暴露管理端接口。  
- **角色隔离**：小程序仅面向学生角色；教师/管理员继续用 Web。  
- **数据格式**：保持 `{code,message,data}` 统一格式，便于小程序复用 axios 封装思路。  
- **调试环境**：使用测试 `appid/secret` 与本地后端（需外网可访问或使用内网穿透）；生产时绑定正式域名和合法回调域。
