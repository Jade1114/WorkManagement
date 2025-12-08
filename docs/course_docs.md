# 📘 Course（学科）API 文档（当前版）

## 基础
- 前缀：`/api/courses`
- 响应：`{ code, message, data }`；业务/权限异常 `400`，未登录/过期 `401`。
- 角色：`teacher` / `admin` 可管理；学生可读列表。
- 软删除：课程有 `deleted` 标志，删除后不在列表/统计中出现。

## 接口

### 1) 创建课程 `POST /api/courses/create`
- 权限：`teacher` / `admin`
- 请求体：`{ "title": "数据结构" }`
- 返回：`{ id, title }`

### 2) 获取全部课程 `GET /api/courses/get`
- 权限：登录
- 返回未删除课程：`[{ id, title }]`

### 3) 课程+作业数 `GET /api/courses/withCount`
- 权限：`teacher` / `admin`
- 返回：`[{ id, title, assignmentCount }]`

### 4) 更新课程名 `PUT /api/courses/update`
- 权限：`teacher` / `admin`
- 请求体：`{ "id": 3, "title": "Web 应用开发" }`
- 返回：`{ id, title }`

### 5) 软删除课程 `DELETE /api/courses/delete/{id}`
- 权限：`teacher` / `admin`
- 返回：`"删除成功"`

> 说明：软删除后，课程被过滤出列表/统计；已有作业仍保留，课程名缺失时返回“未关联课程”。
