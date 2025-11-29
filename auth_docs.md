# 📘 Auth 模块 API 文档（当前版）

## 基础
- 前缀：`/api/auth`
- 响应：`{ code, message, data }`；业务/权限异常 `400`，未登录/过期 `401`。
- 角色：注册仅创建 `student`；`teacher`/`admin` 需由管理员创建（或直接写入 DB）。
- 密码：BCrypt 存储；Token 有效期 1 小时。

## 接口列表

### 1) 学生注册 `POST /api/auth/register`
- 描述：学生自助注册，role 固定为 `student`。
- 请求体：
```json
{ "username": "20240123", "password": "123456" }
```
- 成功：`{ code:200, message:"注册成功，请登录", data:null }`
- 失败示例：`code=400, message="用户名已存在"`

### 2) 登录 `POST /api/auth/login`
- 描述：学生 / 教师 / 管理员登录，返回 JWT。
- 请求体：
```json
{ "username": "teacher001", "password": "teacher123456" }
```
- 成功：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "...",
    "userId": 2,
    "username": "teacher001",
    "role": "teacher"
  }
}
```
- 失败：
  - 账号或密码错误 → `code=400, message="用户名或密码错误"`
  - 账号被禁用（active=false）→ `code=400, message="账号已禁用"`

### 3) 退出 `POST /api/auth/logout`
- 描述：前端退出，后端直接返回成功。
- 请求头：`Authorization: Bearer <token>`
- 成功：`{ code:200, message:"退出成功" }`
