# WorkManagement

一个面向教师与学生的前后端分离作业管理系统，围绕作业发布、提交、批改、成绩查看等教学场景进行统一管理。当前仓库以后端微服务 `cloud/` 与前端 `frontend/` 为主。

## 技术栈

### 后端
- Java 17
- Spring Boot 3
- Spring Cloud Gateway
- OpenFeign
- MyBatis
- MySQL
- JWT
- Nacos Config / Discovery

### 前端
- Vue 3
- Vite
- Pinia
- Vue Router
- Element Plus
- Axios
- ECharts

## 系统结构

### 后端目录
- `cloud/gateway-service`：Gateway 统一入口
- `cloud/user-service`：用户与认证服务
- `cloud/education-service`：课程、作业、提交与教师看板服务

后端当前统一通过 Gateway 对外提供：
- `/user/**`
- `/education/**`

### 前端目录
- `frontend/src/views`：业务页面
- `frontend/src/components`：通用组件
- `frontend/src/layouts`：布局结构
- `frontend/src/stores`：Pinia 状态管理
- `frontend/src/router`：路由与权限控制
- `frontend/src/net`：请求封装

## 核心业务
- 管理员维护用户信息、角色与启用状态
- 教师管理课程、发布作业、查看提交并评分
- 学生查看待提交作业、提交作业、查看评分反馈
- 系统使用 JWT、角色隔离、软删除等机制保证业务边界

## 快速运行

### 1. 环境要求
- Docker / Docker Compose
- Node.js
- Java 17
- Maven
- Nacos

### 2. 启动数据库
```bash
docker compose up -d --build db
```

### 3. 启动后端
在 `cloud/` 目录分别启动服务：
```bash
cd cloud
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl education-service
mvn spring-boot:run -pl gateway-service
```

默认端口：
- `user-service`: `8081`
- `education-service`: `8082`
- `gateway-service`: `8083`

### 4. 启动前端
在 `frontend/` 目录运行：
```bash
npm install
npm run dev
```

前端开发代理会将 `/user` 与 `/education` 转发到本地 Gateway `http://localhost:8083`。

## 文档
- 文档索引：`docs/README.md`
- Cloud 说明：`cloud/README.md`
- API 文档：`docs/api/auth_docs.md`、`docs/api/user_docs.md`、`docs/api/course_docs.md`、`docs/api/assignment_docs.md`、`docs/api/submission_docs.md`

## 当前说明
- 旧单体后端已移除
- 当前默认维护目标是 Cloud 后端与 Web 前端接口对齐
