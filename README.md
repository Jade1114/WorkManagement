# WorkManagement 一键运行指南

本项目前后端分离，提供 docker-compose 一键启动（前端 + 后端 + MySQL）。

## 1. 环境要求
- Docker / Docker Compose
- 端口占用：3306 (MySQL)、8080 (后端)、5173 (前端)

## 2. 快速启动
```bash
docker compose up -d --build
```

启动后：
- 前端：`http://localhost:5173`
- 后端：`http://localhost:8080/api`
- 数据库：`localhost:3306`，库名 `WorkManagement`

> 首次启动会执行 `docker/mysql/init.sql` 预置表结构和示例数据。

## 3. 账号信息（示例数据）
- 老师：`teacher001 / 123456`
- 学生：`student001 / 123456`，`student002 / 123456`

## 4. 服务说明
- compose 定义的服务：
  - `db`：MySQL 8，账号 `app/app_pass`（root 密码 `root_pass`），数据卷 `db_data`
  - `backend`：Spring Boot，profile `mysql`，连接到 `db`
  - `frontend`：Vue3 + Element Plus，Nginx 静态托管，`/api` 代理到 `backend`

## 5. 配置调整
- 修改数据库账号/密码：编辑 `docker-compose.yml` 中 `MYSQL_*`，同步更新 `backend/src/main/resources/application-mysql.yml` 的 `username/password`。
- 修改端口：在 `docker-compose.yml` 的 `ports` 中调整映射，例如前端改为 `"8081:80"`。
- 如需自定义初始化数据：编辑 `docker/mysql/init.sql`，重建 db 容器（`docker compose down -v && docker compose up -d`）。

## 6. 常用命令
- 查看日志：`docker compose logs -f backend`（或 frontend/db）
- 停止服务：`docker compose down`
- 停止并清理数据卷：`docker compose down -v`（会删除数据库数据）

## 7. 目录说明
- `backend/`：Spring Boot 代码与 Dockerfile
- `frontend/`：Vue3 代码、Nginx 配置与 Dockerfile
- `docker/mysql/init.sql`：数据库建表与示例数据
- `docker-compose.yml`：三服务编排文件
