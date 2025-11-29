# 📘 Submission（作业提交）API 文档（当前版）

## 基础
- 前缀：`/api/submissions`
- 响应：`{ code, message, data }`；业务/权限异常 `400`，未登录/过期 `401`。
- 角色：学生提交/查询自己的提交；教师/管理员查看与批改。
- 字段：提交记录包含 `graded`、`score`、`comment`，以及 `submitTime`（数据库记录）。

## 接口

### 1) 学生提交作业 `POST /api/submissions/submit`
- 权限：`student`
- 请求体：`{ "assignmentId": 10, "content": "答案内容" }`
- 返回：`{ id, assignmentId, studentId, content, score, comment, graded }`

### 2) 学生查看单个作业提交 `GET /api/submissions/my?assignmentId=10`
- 权限：`student`
- 返回：单条提交记录（含评分、批注）

### 3) 教师/管理员查看某作业提交列表 `GET /api/submissions/list?assignmentId=10`
- 权限：`teacher` / `admin`
- 返回：`[{ id, studentId, studentUsername, content, score, graded }]`

### 4) 教师/管理员批改 `POST /api/submissions/grade`
- 权限：`teacher` / `admin`
- 请求体：`{ "submissionId": 100, "score": 95, "comment": "完成得很好" }`
- 成功：`"批改成功"`

### 5) 教师/管理员查看所有提交 `GET /api/submissions/all`
- 权限：`teacher` / `admin`
- 返回：`[{ submissionId, assignmentId, assignmentTitle, courseId, courseTitle, studentId, studentName, assignmentContent, submitContent, submitTime, graded, score, comment }]`

### 6) 学生查看自己所有提交列表 `GET /api/submissions/my/list`
- 权限：`student`
- 返回：`[{ submissionId, assignmentId, assignmentTitle, courseId, courseTitle, submitContent, comment, graded, score }]`

### 7) 仪表盘最近提交 `GET /api/teacher/recent/submissions`
- 权限：`teacher` / `admin`
- 返回：`[{ submissionId, studentName, assignmentTitle, courseTitle, graded, score, submitTime }]`

> 权限不足时返回 `code=400, message="权限不足"`。
