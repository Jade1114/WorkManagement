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
  "message": "success",
  "data": {
    "id": 100,
    "assignmentId": 10,
    "studentId": 3,
    "content": "这是我的作业内容",
    "score": null,
    "comment": null,
    "graded": false
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

# 5. **老师查看所有提交列表（用于首页统计/列表）**

## **GET /api/submissions/all**

### 描述

老师查看系统内全部提交，包含课程/作业/学生的基础信息。

### 权限

```
@RequiresRole("teacher")
```

### 返回示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "submissionId": 100,
      "assignmentId": 10,
      "assignmentTitle": "第1次作业",
      "courseId": 1,
      "courseTitle": "Java 程序设计",
      "studentId": 3,
      "studentName": "20240123",
      "assignmentContent": "完成 Java 基础练习题",
      "submitContent": "这是我的作业内容",
      "submitTime": null,
      "graded": false,
      "score": null,
      "comment": null
    }
  ]
}
```

> 目前未记录提交时间字段，`submitTime` 恒为 null。

---

# 6. **学生查看自己所有作业提交记录**

## **GET /api/submissions/my/list**

### 描述

学生查看自己在所有课程下的提交列表。

### 权限

```
@RequiresRole("student")
```

### 返回示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "submissionId": 100,
      "assignmentId": 10,
      "assignmentTitle": "第1次作业",
      "courseId": 1,
      "courseTitle": "Java 程序设计",
      "submitContent": "作业内容",
      "comment": "完成得很好！",
      "graded": true,
      "score": 95
    }
  ]
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
| 老师查看全部提交   | /api/submissions/all   | GET  | teacher |
| 学生查看所有提交列表 | /api/submissions/my/list | GET | student |

---
