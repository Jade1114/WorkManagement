# 📘 Course 模块 API 文档（Cloud 当前版）

## 基础
- Gateway 前缀：`/education/courses`
- 响应：`{ code, message, data }`
- 业务/权限异常：`400`
- 未登录或 token 无效：`401`
- 软删除：课程删除后不再出现在课程列表与统计中

## 接口

### 1) 创建课程 `POST /education/courses`
- 权限：`teacher` / `admin`
- 请求体：
```json
{ "title": "数据结构" }
```
- 返回：
```json
{ "id": 1, "title": "数据结构" }
```

### 2) 获取课程列表 `GET /education/courses`
- 权限：登录
- 返回：
```json
[
  { "id": 1, "title": "数据结构" }
]
```

### 3) 获取课程及作业数 `GET /education/courses?includeAssignmentCount=true`
- 权限：`teacher` / `admin`
- 返回：
```json
[
  { "id": 1, "title": "数据结构", "assignmentCount": 3 }
]
```

### 4) 更新课程 `PUT /education/courses/{id}`
- 权限：`teacher` / `admin`
- 请求体：
```json
{ "title": "Web 应用开发" }
```
- 返回：
```json
{ "id": 1, "title": "Web 应用开发" }
```

### 5) 删除课程 `DELETE /education/courses/{id}`
- 权限：`teacher` / `admin`
- 成功返回：`"删除成功"`
