# 📘 Assignment（作业）API 文档（当前版）

## 基础
- 前缀：`/api/assignments`
- 响应：`{ code, message, data }`；业务/权限异常 `400`，未登录/过期 `401`。
- 角色：`teacher` / `admin` 可创建和查看全部作业；`student` 可查看课程作业/未提交列表。
- 课程软删除：被删除课程的作业仍保留，未匹配课程时前端显示“未关联课程”。

## 接口

### 1) 创建作业 `POST /api/assignments/create`
- 权限：`teacher` / `admin`（作业归属当前登录老师）
- 请求体：
```json
{
  "courseId": 1,
  "title": "第1次作业",
  "content": "完成 Java 基础练习题",
  "deadline": "2024-12-31T23:59:59"
}
```
- 返回：`{ id, courseId, title, content, deadline }`

### 2) 获取某课程作业 `GET /api/assignments/list?courseId=1`
- 权限：登录
- 返回：`[{ id, courseId, title, content, deadline }]`

### 3) 查看全部作业（教师/管理员）`GET /api/assignments/all`
- 权限：`teacher` / `admin`
- 说明：教师仅返回自己发布的作业，管理员返回全部
- 返回：`[{ id, courseId, courseTitle, title, content, deadline }]`

### 4) 学生未提交列表 `GET /api/assignments/pending`
- 权限：`student`（根据当前学生过滤）
- 返回：`[{ id, courseId, courseTitle, title, content, deadline }]`

### 5) 仪表盘数据（教师/管理员）
- 最近作业：`GET /api/teacher/recent/assignments` → `[{ id, courseId, courseTitle, title, deadline, createdAt }]`
- 统计概览：`GET /api/teacher/stats` → `{ pendingSubmissions, assignments, students, courses }`
- 数据大屏：`GET /api/teacher/dataScreen` →
```json
{
  "assignmentsByCourse": [{ "courseTitle": "数据结构", "assignments": 2 }],
  "submissionStatus": { "graded": 2, "pending": 3 },
  "submissionsByDate": [{ "date": "2024-12-01", "count": 5 }]
}
```

> 权限不足时返回 `code=400, message="权限不足"`。
