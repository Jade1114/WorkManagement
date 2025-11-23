# 📘 User 模块 API 文档（最终版）

## 基础说明

### **接口 URL 前缀**

```
/api/users
```

### **统一响应格式**

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### **错误码规范**

| code | 含义                |
| ---- | ----------------- |
| 200  | 成功                |
| 400  | 参数错误 / 业务异常       |
| 401  | 未登录 / token 无效或过期 |
| 403  | 无权限               |
| 500  | 服务器错误             |

### 角色说明

| 角色      | 说明         |
| ------- | ---------- |
| student | 学生，由注册接口创建 |
| teacher | 老师，手动插入数据库 |

---

# 1. **获取当前用户信息**

## **GET /api/users/me**

### 描述

返回当前登录用户的信息（学生或老师都可调用）。

### 请求头

```
Authorization: Bearer <token>
```

### 响应（成功）

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "20240123",
    "role": "student"
  }
}
```

---

# 2. **修改密码**

## **PUT /api/users/changePassword**

### 描述

学生/老师均可修改自己的密码。

### 请求头

```
Authorization: Bearer <token>
```

### 请求参数（JSON）

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

字段说明：

| 字段          | 必填 | 说明          |
| ----------- | -- | ----------- |
| oldPassword | 是  | 旧密码         |
| newPassword | 是  | 新密码（>= 6 位） |

---

### 响应（成功）

```json
{
  "code": 200,
  "message": "success",
  "data": "密码修改成功"
}
```

---

### 响应（失败：旧密码错误）

```json
{
  "code": 400,
  "message": "旧密码错误",
  "data": null
}
```

---

# 3. **获取所有学生列表（仅老师可用）**

## **GET /api/users/students**

### 描述

老师查看系统内所有学生。

### 权限控制

```
@RequiresRole("teacher")
```

### 请求头

```
Authorization: Bearer <token>
```

---

### 响应（成功）

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "username": "20240123",
      "role": "student"
    },
    {
      "id": 1,
      "username": "teacher001",
      "role": "teacher"
    }
  ]
}
```

> 当前实现直接返回系统内所有用户，前端会自行筛选出学生。

# 🎯 User 模块 API 文档总结

你最终的 User 模块 API 包含：

| 接口                            | 描述        | 权限              |
| ----------------------------- | --------- | --------------- |
| GET /api/users/me             | 获取当前用户信息  | 登录              |
| PUT /api/users/changePassword | 修改密码      | 登录              |
| GET /api/users/students       | 获取学生列表    | teacher         |

---
