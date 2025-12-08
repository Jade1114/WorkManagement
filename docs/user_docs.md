# 📘 User 模块 API 文档（当前版）

## 基础
- 前缀：`/api/users`
- 响应：`{ code, message, data }`；业务/权限异常 `400`，未登录/过期 `401`。
- 角色：`student`（注册生成）、`teacher`/`admin`（管理员创建）。
- `active`：用户启用状态，admin 可禁用；禁用用户无法登录。

## 接口

### 1) 获取当前用户 `GET /api/users/me`
- 权限：登录
- 返回：`{ id, username, role }

### 2) 修改密码 `PUT /api/users/changePassword`
- 权限：登录
- 请求体：`{ "oldPassword": "123456", "newPassword": "654321" }`
- 成功：`"密码修改成功"`

### 3) 获取学生列表 `GET /api/users/students`
- 权限：`teacher` / `admin`
- 返回：所有用户（前端会过滤学生），字段 `{ id, username, role }`

### 4) 管理员获取所有用户 `GET /api/users/admin/list`
- 权限：`admin`
- 返回：`[{ id, username, role, active }]`

### 5) 管理员更新用户 `PUT /api/users/admin/update`
- 权限：`admin`
- 请求体（字段可选）：
```json
{ "userId": 6, "role": "teacher", "active": true }
```
- 成功：`"更新成功"`
