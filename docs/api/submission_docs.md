# 📘 Submission 模块 API 文档（Cloud 当前版）

## 基础
- Gateway 前缀：`/education/submissions`
- 响应：`{ code, message, data }`
- 业务/权限异常：`400`
- 未登录或 token 无效：`401`

## 接口

### 1) 学生提交作业 `POST /education/submissions`
- 权限：`student`
- 请求体：
```json
{
  "assignmentId": 10,
  "content": "答案内容"
}
```
- 返回：
```json
{
  "id": 100,
  "assignmentId": 10,
  "studentId": 3,
  "content": "答案内容",
  "score": null,
  "comment": null,
  "graded": false
}
```

### 2) 查看某作业提交列表 `GET /education/submissions?assignmentId=10`
- 权限：`teacher` / `admin`
- 说明：`teacher` 仅可查看自己作业的提交
- 返回：
```json
[
  {
    "id": 100,
    "studentId": 3,
    "studentUsername": "student01",
    "content": "答案内容",
    "score": null,
    "graded": false
  }
]
```

### 3) 查看全部提交总览 `GET /education/submissions`
- 权限：`teacher` / `admin`
- 说明：`teacher` 仅可查看自己作业的提交
- 返回：
```json
[
  {
    "submissionId": 100,
    "assignmentId": 10,
    "assignmentTitle": "第 1 次作业",
    "courseId": 1,
    "courseTitle": "数据结构",
    "studentId": 3,
    "studentName": "student01",
    "assignmentContent": "完成 Java 基础练习题",
    "submitContent": "答案内容",
    "submitTime": "2026-04-19T20:00:00",
    "graded": false,
    "score": null,
    "comment": null
  }
]
```

### 4) 查看我的提交列表 `GET /education/submissions/my`
- 权限：`student`
- 返回：
```json
[
  {
    "submissionId": 100,
    "assignmentId": 10,
    "assignmentTitle": "第 1 次作业",
    "courseId": 1,
    "courseTitle": "数据结构",
    "submitContent": "答案内容",
    "comment": null,
    "graded": false,
    "score": null
  }
]
```

### 5) 查看我对某作业的提交详情 `GET /education/submissions/my?assignmentId=10`
- 权限：`student`
- 返回：
```json
{
  "id": 100,
  "assignmentId": 10,
  "studentId": 3,
  "content": "答案内容",
  "score": 95,
  "comment": "完成得很好",
  "graded": true
}
```

### 6) 批改提交 `PATCH /education/submissions/{submissionId}`
- 权限：`teacher` / `admin`
- 请求体：
```json
{
  "score": 95,
  "comment": "完成得很好"
}
```
- 成功返回：空 `data`
