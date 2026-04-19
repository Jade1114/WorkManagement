# 📘 User 模块 API 文档（Cloud 当前版）

## 基础
- Gateway 前缀：`/user/users`
- 响应：`{ code, message, data }`
- 业务/权限异常：`400`
- 未登录或 token 无效：`401`

## 接口

### 1) 获取当前用户 `GET /user/users/me`
- 权限：登录
- 返回：
```json
{
  "id": 1,
  "username": "admin",
  "role": "admin"
}
```

### 2) 修改当前用户密码 `PUT /user/users/me/password`
- 权限：登录
- 请求体：
```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```
- 成功返回：`"密码修改成功"`

### 3) 获取学生列表 `GET /user/users?role=student`
- 权限：`teacher` / `admin`
- 返回：
```json
[
  { "id": 3, "username": "student01", "role": "student" }
]
```

### 4) 获取用户列表 `GET /user/users`
- 权限：`admin`
- 返回：
```json
[
  { "id": 1, "username": "admin", "role": "admin", "active": true },
  { "id": 2, "username": "teacher01", "role": "teacher", "active": true }
]
```

### 5) 更新指定用户 `PATCH /user/users/{targetUserId}`
- 权限：`admin`
- 请求体字段可选，但至少传一个：
```json
{
  "role": "teacher",
  "active": true
}
```
- 成功返回：`"更新成功"`
- 说明：
  - `role` 仅允许 `admin | teacher | student`
  - `active` 表示启用状态
