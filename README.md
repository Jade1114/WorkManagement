# WorkManagement

WorkManagement 是一个围绕教学场景构建的作业管理系统，当前仓库以 **Spring Cloud 微服务后端** 与 **Vue 3 前端** 为核心，覆盖用户认证、课程管理、作业发布、作业提交、教师批改与统计看板等完整业务链路。

这个项目最初源于一个标准业务型教务系统，后续逐步演进为微服务版本，目的不是单纯把项目“拆开”，而是通过真实业务场景练习：
- Spring Cloud 基础组件协作
- 微服务边界划分
- Gateway 统一入口
- Feign 服务间调用
- Nacos 注册发现与配置管理
- 前后端接口对齐
- Docker 化一键运行

## 项目定位

如果把这个项目放在学习路径里看，它承担的是一个很明确的角色：
- 在业务复杂度适中的前提下，练习从单体到微服务的演进过程
- 用真实的“用户域 + 教务域”拆分来理解服务边界，而不是只做组件堆叠
- 把认证、授权、配置治理、服务调用、运行编排这些 Spring Cloud 关键问题串成一个完整系统

因此，它既是一个可演示的业务系统，也是一个偏工程化的 Spring Cloud 学习项目。

## 当前仓库结构

```text
.
├── cloud/              # Spring Cloud 微服务后端
├── frontend/           # Vue 3 前端
├── docker/             # Docker 启动依赖与初始化文件
├── docker-compose.yml  # 一键启动编排
└── README.md
```

## 技术栈

### 后端
- Java 17
- Spring Boot 3
- Spring Cloud Gateway
- Spring Cloud OpenFeign
- Spring Cloud Alibaba Nacos Discovery / Config
- MyBatis
- MySQL
- JWT
- Hibernate Validator

### 前端
- Vue 3
- Vite
- Pinia
- Vue Router
- Element Plus
- Axios
- ECharts

### 运行与交付
- Docker / Docker Compose
- Nginx

## 核心业务能力

### 用户与角色
- `admin`：系统管理者，负责用户管理与教务管理类操作
- `teacher`：教学执行者，负责课程、作业、批改与教师侧统计
- `student`：学习执行者，负责查看作业、提交作业、查看成绩与反馈

### 已实现业务
- 用户注册、登录、登出、当前用户信息、修改密码
- 管理员用户列表、角色调整、启用/禁用
- 课程列表、课程统计、创建、更新、删除
- 作业创建、教师/管理员查看作业、按课程查看作业
- 学生待提交作业列表
- 学生提交作业
- 教师/管理员查看提交列表与提交总览
- 教师批改作业
- 学生查看自己的提交记录与提交详情
- 教师端统计概览、最近作业、最近提交、Top submitters、数据看板

## 当前微服务架构

### 服务划分
- `gateway-service`
  - 统一入口
  - JWT 校验
  - 身份透传
  - 路由分发
  - 统一 401 返回

- `user-service`
  - 登录 / 注册 / 登出
  - 用户管理
  - JWT 签发
  - 内部用户校验、用户摘要、人数统计

- `education-service`
  - 课程、作业、提交、教师看板
  - 通过 Feign 调用 `user-service` 获取用户侧协作能力

### 架构边界
- Gateway 负责“你是谁”
- 业务服务负责“你能不能做这件事”
- 用户数据主权归 `user-service`
- 教务业务主权归 `education-service`

### 对外接口前缀
- `/user/**`
- `/education/**`

## Docker 启动

项目当前支持纯 Docker 启动，目标是做到：
- MySQL、Nacos、前后端与微服务全部进入容器
- 本机不额外依赖数据库与注册中心
- 一条命令启动整套系统

### 启动命令

```bash
docker compose up --build
```

### 默认访问地址
- 前端：`http://localhost`
- Gateway：`http://localhost:8083`
- Nacos：`http://localhost:8848/nacos`
- MySQL：`localhost:3307`

### 说明
首次启动前，请确认以下 Nacos 配置已准备好：
- `docker/nacos/db-config.yaml`
- `docker/nacos/jwt-config.yaml`

项目会通过 `nacos-config-init` 容器自动将这些配置写入 Nacos。

## 这个项目目前已经体现了什么

从学习 Spring Cloud 的角度看，这个项目目前已经完成了第一阶段的核心训练：
- 从单体业务系统向微服务系统演进
- 完成服务拆分与边界划分
- 完成 Gateway + Feign + Nacos + JWT 的主链路串联
- 完成前端接口切换与整套系统联调
- 完成基于 Docker Compose 的整体运行编排

## 下一步方向

当前项目后续更有价值的方向，不再只是继续补 CRUD，而是围绕微服务核心问题继续深入：
- 业务逻辑与业务边界再梳理
- 服务治理（超时、降级、幂等等）
- 可观测性（traceId、日志链路、排障能力）
- 代码层面的抽象、收口、解耦与工程化优化
- 技术文档整理与简历沉淀

## 一句话总结

WorkManagement 现在不是一个单纯的“教务 CRUD 项目”，而是一套围绕真实教学场景构建、并已经完成微服务化与 Docker 化落地的 Spring Cloud 学习型系统。
