# 📘 Assignment 模块 API 文档（Cloud 当前版）

## 基础
- Gateway 前缀：`/education/assignments`
- 响应：`{ code, message, data }`
- 业务/权限异常：`400`
- 未登录或 token 无效：`401`

## 接口

### 1) 创建作业 `POST /education/assignments`
- 权限：`teacher` / `admin`
- 请求体：
```json
{
  "courseId": 1,
  "title": "第 1 次作业",
  "content": "完成 Java 基础练习题",
  "deadline": "2026-04-30T23:59:59"
}
```
- 返回：
```json
{
  "id": 10,
  "courseId": 1,
  "teacherId": 2,
  "title": "第 1 次作业",
  "content": "完成 Java 基础练习题",
  "deadline": "2026-04-30T23:59:59"
}
```

### 2) 获取作业列表 `GET /education/assignments`
- 权限：`teacher` / `admin`
- 说明：
  - `teacher` 仅返回自己发布的作业
  - `admin` 返回全部作业
- 返回字段示例：
```json
[
  {
    "id": 10,
    "courseId": 1,
    "courseTitle": "数据结构",
    "teacherId": 2,
    "title": "第 1 次作业",
    "content": "完成 Java 基础练习题",
    "deadline": "2026-04-30T23:59:59"
  }
]
```

### 3) 按课程获取作业 `GET /education/assignments?courseId=1`
- 权限：登录
- 返回：同上列表结构

### 4) 获取待提交作业 `GET /education/assignments?status=pending`
- 权限：`student`
- 返回：
```json
[
  {
    "id": 10,
    "courseId": 1,
    "courseTitle": "数据结构",
    "title": "第 1 次作业",
    "content": "完成 Java 基础练习题",
    "deadline": "2026-04-30T23:59:59"
  }
]
```

## 教师看板相关
- 统计概览：`GET /education/teachers/me/stats`
- 最近作业：`GET /education/teachers/me/recent/assignments`
- 最近提交：`GET /education/teachers/me/recent/submissions`
- Top submitters：`GET /education/teachers/me/top-submitters`
- 数据大屏：`GET /education/teachers/me/data-screen`
