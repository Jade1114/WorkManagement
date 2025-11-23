# 📘 Assignment 模块 API 文档（最终版）

## 基础说明

- 接口前缀：`/api/assignments`
- 统一响应：`{ code: 200, message: "success", data: ... }`（业务异常 400，权限异常 401）
- 所有接口需携带 `Authorization: Bearer <token>`，除非特别说明
- 角色权限：老师可创建/查看所有作业，学生可查看课程作业及自己的未提交列表

---

# 1. 老师创建作业

**POST /api/assignments/create**  
权限：`teacher`

请求体：
```json
{
  "courseId": 1,
  "title": "第1次作业",
  "content": "完成 Java 基础练习题",
  "deadline": "2024-12-31T23:59:59"
}
```

成功响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "courseId": 1,
    "title": "第1次作业",
    "content": "完成 Java 基础练习题",
    "deadline": "2024-12-31T23:59:59"
  }
}
```

---

# 2. 获取某课程的全部作业

**GET /api/assignments/list?courseId=1**  
权限：已登录（teacher/student）

返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 10, "courseId": 1, "title": "第1次作业", "content": "完成 Java 基础练习题", "deadline": "2024-12-31T23:59:59" },
    { "id": 11, "courseId": 1, "title": "第2次作业", "content": "阅读 Java 异常机制章节", "deadline": "2024-11-20T23:59:59" }
  ]
}
```

---

# 3. 老师获取全部作业列表

**GET /api/assignments/all**  
权限：`teacher`

返回字段包含课程名称：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 10,
      "courseId": 1,
      "courseTitle": "Java 程序设计",
      "title": "第1次作业",
      "content": "完成 Java 基础练习题",
      "deadline": "2024-12-31T23:59:59"
    }
  ]
}
```

---

# 4. 学生获取未提交的作业

**GET /api/assignments/pending**  
权限：`student`（自动按当前登录学生过滤）

返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 12,
      "courseId": 1,
      "courseTitle": "Java 程序设计",
      "title": "第2次作业",
      "content": "阅读 Java 异常机制章节",
      "deadline": "2024-11-20T23:59:59"
    }
  ]
}
```

---

# 5. 老师首页统计数据

**GET /api/teacher/stats**  
权限：`teacher`

返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "pendingSubmissions": 3,
    "assignments": 8,
    "students": 26,
    "courses": 4
  }
}
```

---

# 📂 Assignment 表结构

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

# 🎯 Assignment 模块功能一览

| 功能               | URL                      | 方法 | 权限              |
| ---------------- | ------------------------ | ---- | ----------------- |
| 创建作业           | /api/assignments/create  | POST | teacher           |
| 获取课程作业列表     | /api/assignments/list    | GET  | teacher/student   |
| 老师查看全部作业     | /api/assignments/all     | GET  | teacher           |
| 学生查看未提交作业列表 | /api/assignments/pending | GET  | student           |
| 老师统计数据        | /api/teacher/stats       | GET  | teacher           |
