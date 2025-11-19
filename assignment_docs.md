# 📘 Assignment 模块 API 文档（最终版）

## 基础说明

### 接口前缀

```
/api/assignments
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
| teacher | 创建作业、查看作业列表 |
| student | 查看作业列表      |

---

# 1. **老师创建作业**

## **POST /api/assignments/create**

### 描述

老师为指定课程创建作业。

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
  "courseId": 1,
  "title": "第1次作业",
  "content": "完成 Java 基础练习题",
  "deadline": "2024-12-31 23:59:59"
}
```

字段说明：

| 字段       | 必填 | 说明         |
| -------- | -- | ---------- |
| courseId | 是  | 所属课程 ID    |
| title    | 是  | 作业标题       |
| content  | 否  | 作业内容（可为空）  |
| deadline | 否  | 截止时间（推荐保留） |

---

### 成功响应示例

```json
{
  "code": 200,
  "message": "作业创建成功",
  "data": {
    "id": 10
  }
}
```

---

# 2. **获取某课程的全部作业（含作业详情）**

## **GET /api/assignments/list?courseId=1**

### 描述

返回指定课程下的所有作业，包含作业详情字段。
（因为你不需要单独的作业详情接口）

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
      "id": 10,
      "courseId": 1,
      "title": "第1次作业",
      "content": "完成 Java 基础练习题",
      "deadline": "2024-12-31 23:59:59"
    },
    {
      "id": 11,
      "courseId": 1,
      "title": "第2次作业",
      "content": "阅读 Java 异常机制章节",
      "deadline": "2024-11-20 23:59:59"
    }
  ]
}
```

---

# 📂 Assignment 表结构（最终）

```sql
CREATE TABLE assignment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NULL,
    deadline DATETIME NULL
);
```

---

# 🎯 Assignment 模块功能总结

| 功能            | URL                     | 方法   | 权限              |
| ------------- | ----------------------- | ---- | --------------- |
| 创建作业          | /api/assignments/create | POST | teacher         |
| 获取课程作业列表（含详情） | /api/assignments/list   | GET  | teacher/student |