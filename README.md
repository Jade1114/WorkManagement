# WorkManagement 一键运行指南

本项目前后端分离，仓库内的 `docker-compose.yml` 只提供 **MySQL** 服务（端口 3307），供前后端本地开发使用；前后端自行手动启动。

## 1. 环境要求
- Docker / Docker Compose
- 端口占用：3307 (MySQL)，后端/前端按本地端口自定

## 2. 快速启动数据库
```bash
docker compose up -d --build db
```

启动后：
- 数据库：`127.0.0.1:3307`，库名 `WorkManagement`
- 账号：`app / app_pass`（root：`root / root_pass`）
- 首次启动自动执行 `docker/mysql/init.sql` 预置表结构与示例数据，强制使用 `utf8mb4` 防止中文乱码。

> 如需重新初始化数据：`docker compose down -v && docker compose up -d --build db`

## 3. 默认账号
- 管理员：`admin / admin123`
- 教师：`teacher001 / teacher123456`，`teacher002 / teacher123456`
- 学生：`student001 / student123456`，`student002 / student123456`，`student003 / student123456`

密码在数据库中存储为 BCrypt，`init.sql` 会在重复执行时同步更新密码、角色与启用状态。

## 4. 后端 / 前端对接
- 后端 MySQL 配置：`backend/src/main/resources/application-docker.yml`（指向 `localhost:3307/WorkManagement`）。
- 启动后端（示例）：`./mvnw spring-boot:run` 或 IDE 运行，确保 `Active profiles: docker`。
- 前端默认使用代理 `/api` 指向后端，启动方式参考前端目录说明。

## 5. Navicat / MySQL 客户端连接
- Host：`127.0.0.1`
- Port：`3307`
- User/Password：`app / app_pass`（或 `root / root_pass`）
- 字符集：选择 `utf8mb4`，避免查看数据时出现乱码。

## 6. 常用命令
- 查看日志：`docker compose logs -f db`
- 停止服务：`docker compose down`
- 停止并清理数据卷：`docker compose down -v`（删除数据库数据）

## 7. 目录说明
- `backend/`：Spring Boot 代码
- `frontend/`：Vue3 代码、Nginx 配置
- `docker/mysql/init.sql`：建表 & 示例数据脚本（utf8mb4，含软删除/禁用字段与创建/提交时间字段）
- `docker-compose.yml`：MySQL 编排配置（仅数据库）
- `auth_docs.md` / `user_docs.md` / `course_docs.md` / `assignment_docs.md` / `submission_docs.md`：后端 API 文档（当前版）
