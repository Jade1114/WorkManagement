# 📘 Submission 模块 API 文档（最终版）

## 基础说明

接口前缀：

```
/api/submissions
```

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

---

# 1. **学生提交作业**

## **POST /api/submissions/submit**

### 描述

学生对某个作业进行提交。

### 权限

```
@RequiresRole("student")
```

### 请求头

```
Authorization: Bearer <token>
```

### 请求体（JSON）

```json
{
  "assignmentId": 10,
  "content": "这是我的作业内容（可以是文本、代码、链接等）"
}
```

字段说明：

| 字段           | 必填 | 说明    |
| ------------ | -- | ----- |
| assignmentId | 是  | 作业 ID |
| content      | 是  | 提交的内容 |

---

### 提交成功响应

```json
{
  "code": 200,
  "message": "提交成功",
  "data": {
    "id": 100
  }
}
```

---

# 2. **学生查看自己某作业的提交记录**

## **GET /api/submissions/my?assignmentId=10**

### 描述

学生查看自己对某作业的提交。

### 权限

```
@RequiresRole("student")
```

### 成功响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 100,
    "assignmentId": 10,
    "studentId": 3,
    "content": "这是我的作业内容",
    "score": 95,
    "comment": "完成得很好！",
    "graded": true
  }
}
```

---

# 3. **老师查看某个作业的所有提交**

## **GET /api/submissions/list?assignmentId=10**

### 描述

老师查看某个作业下所有学生提交。

### 权限

```
@RequiresRole("teacher")
```

---

### 返回示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 100,
      "studentId": 3,
      "studentUsername": "20240123",
      "content": "这是学生的作业内容",
      "score": 95,
      "graded": true
    },
    {
      "id": 101,
      "studentId": 4,
      "studentUsername": "20240124",
      "content": "这是另一个学生的提交",
      "score": null,
      "graded": false
    }
  ]
}
```

---

# 4. **老师批改提交（评分与评语）**

## **POST /api/submissions/grade**

### 描述

老师为学生提交评分。

### 权限

```
@RequiresRole("teacher")
```

### 请求体（JSON）

```json
{
  "submissionId": 100,
  "score": 95,
  "comment": "完成得很好！"
}
```

字段说明：

| 字段           | 必填 | 说明    |
| ------------ | -- | ----- |
| submissionId | 是  | 提交 ID |
| score        | 是  | 分数    |
| comment      | 否  | 批语    |

---

### 成功响应

```json
{
  "code": 200,
  "message": "批改成功",
  "data": null
}
```

---

# 📂 Submission 表结构（最终版）

```sql
CREATE TABLE submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    assignment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    score INT NULL,
    comment TEXT NULL,
    graded BOOLEAN DEFAULT FALSE
);
```

### 说明：

* `graded` = true 表示已经批改
* score 和 comment 只有老师批改后才有

---

# 🎯 Submission 模块功能总结

| 功能         | URL                     | 方法   | 权限      |
| ---------- | ----------------------- | ---- | ------- |
| 学生提交作业     | /api/submissions/submit | POST | student |
| 学生查看提交     | /api/submissions/my     | GET  | student |
| 老师查看作业提交列表 | /api/submissions/list   | GET  | teacher |
| 老师批改作业提交   | /api/submissions/grade  | POST | teacher |

---
