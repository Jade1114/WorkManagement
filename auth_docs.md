# 📘 Auth 模块 API 文档（最终版）

## 基础说明

### **接口 URL 前缀**

```
/api/auth
```

### **统一响应格式（所有接口遵循）**

```json
{
  "code": 0,            // 0 表示成功，其他为错误码
  "message": "success", // 成功或错误信息
  "data": {} | null     // 返回数据
}
```

### **错误码规范**

| code | 含义                |
|------| ----------------- |
| 200  | 成功                |
| 400  | 参数错误 / 业务异常       |
| 401  | 未登录 / token 无效或过期 |
| 403  | 无权限               |
| 500  | 服务器错误             |

---

# 1. **学生注册接口**

## **POST /api/auth/register**

### 描述

* 学生自行注册账号
* 注册成功后需要手动登录
* role 固定为 `"student"`

---

### 请求参数（JSON）

```json
{
  "username": "20240123",
  "password": "123456"
}
```

字段说明：

| 字段       | 必填 | 说明              |
| -------- | -- | --------------- |
| username | 是  | 用户名（通常为学号），需要唯一 |
| password | 是  | 登录密码            |

---

### 响应（成功）

```json
{
  "code": 200,
  "message": "注册成功，请登录",
  "data": null
}
```

---

### 响应（失败示例）

用户名已存在：

```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

---

# 2. **登录接口**

## **POST /api/auth/login**

### 描述

* 用户登录（学生/老师/管理员）
* 返回 JWT Token
* Token 通过前端保存（pinia）

---

### 请求参数（JSON）

```json
{
  "username": "20240123",
  "password": "123456"
}
```

---

### 响应（成功）

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "20240123",
      "role": "student"
    }
  }
}
```

---

### 响应（失败示例）

用户名/密码错误：

```json
{
  "code": 400,
  "message": "用户名或密码错误",
  "data": null
}
```


---

# 3. **退出接口**

## **POST /api/auth/logout**

### 描述

* 前端请求退出登录
* 后端直接退出

---

### 请求头

```
Authorization: Bearer <token>
```

---

### 响应（成功）

```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

---

# 6. **JWT 与权限说明（附录）**

### Token 携带方式

```
Authorization: Bearer <token>
```

### Token 内容（payload 示例）

```json
{
  "userId": 1,
  "role": "student",
  "exp": 1712345678
}
```

---

### Auth 模块使用的安全机制：

| 功能              | 是否包含 | 实现方式                |
| --------------- | ---- |---------------------|
| JWT 登录认证        | ✔    | jjwt / auth0 jwt    |
| Token 过期        | ✔    | exp 属性              |
| 退出机制            | ✔    | 直接退出                |
| 权限注解            | ✔    | @RequiresRole + AOP |
| 全局异常处理          | ✔    | @ControllerAdvice   |

---