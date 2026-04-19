# 📘 Auth 模块 API 文档（Cloud 当前版）

## 基础
- Gateway 前缀：`/user/auth`
- 响应：`{ code, message, data }`
- 业务/校验异常：`400`
- 未登录或 token 无效：`401`

## 接口

### 1) 学生注册 `POST /user/auth/register`
- 权限：匿名
- 请求体：
```json
{
  "username": "student01",
  "password": "123456"
}
```
- 成功返回：`"注册成功"`
- 说明：当前注册默认创建 `student` 角色用户

### 2) 登录 `POST /user/auth/login`
- 权限：匿名
- 请求体：
```json
{
  "username": "admin",
  "password": "123456"
}
```
- 成功返回：
```json
{
  "token": "jwt-token",
  "userId": 1,
  "username": "admin",
  "role": "admin"
}
```

### 3) 退出登录 `POST /user/auth/logout`
- 权限：登录
- 成功返回：`"退出成功"`
- 说明：当前为无状态登出，占位成功返回
