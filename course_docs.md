# 📘 Course 模块 API 文档（最终版）

## 基础说明

### 接口前缀

```
/api/courses
```

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 权限说明

| 角色      | 权限          |
| ------- | ----------- |
| teacher | 创建课程、查看课程列表 |
| student | 查看课程列表      |

### 课程业务说明

* 系统只有一个老师（手动插入 DB）
* 老师可以创建课程
* 所有学生默认属于所有课程（无选课/加入课程逻辑）
* 学生与老师均可查看课程列表
* 不需要课程详情页
* 不需要 description / created_at 字段

---

# 1. **老师创建课程**

## **POST /api/courses/create**

### 描述

老师创建课程，仅需提供课程名称。

### 权限

```
@RequiresRole("teacher")
```

### 请求头

```
Authorization: Bearer <token>
```

### 请求体（JSON）

```json
{
  "title": "Java 程序设计"
}
```

字段说明：

| 字段    | 必填 | 说明   |
| ----- | -- | ---- |
| title | 是  | 课程名称 |

---

### 成功响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "Java 程序设计"
  }
}
```

---

# 2. **获取所有课程（老师与学生共用）**

## **GET /api/courses/get**

### 描述

返回系统中的所有课程。

* 老师用于管理课程
* 学生用于查看课程中的作业

### 权限

```
登录即可（teacher 或 student）
```

---

### 成功响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "Java 程序设计"
    },
    {
      "id": 2,
      "title": "数据库基础"
    }
  ]
}
```

---

# 3. **老师查看课程及作业数量**

## **GET /api/courses/withCount**

### 描述

返回所有课程及其已发布作业数量。

### 权限

```
@RequiresRole("teacher")
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 1, "title": "Java 程序设计", "assignmentCount": 4 },
    { "id": 2, "title": "数据库基础", "assignmentCount": 1 }
  ]
}
```

---

# 📂 数据库结构（课程表）

```sql
CREATE TABLE course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL
);
```

---

# 🎯 Course 模块功能总结

| 功能     | URL                 | 方法   | 权限      |
| ------ | ------------------- | ---- | ------- |
| 创建课程   | /api/courses/create | POST | teacher |
| 获取所有课程 | /api/courses/get    | GET  | 登录      |
| 课程+作业数量 | /api/courses/withCount | GET  | teacher   |

---
