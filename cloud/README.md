# WorkManagement Cloud

> 这是基于 `WorkManagement` 单体项目逐步演进出来的 Spring Cloud 学习版工程。
> 当前目标不是一次性把所有单体功能迁完，而是围绕真实业务链，逐步把：
> - 服务拆分
> - 注册发现
> - Gateway 统一入口
> - OpenFeign 服务协作
> - Nacos Config 配置治理
> - 角色边界与业务编排
> 真正落到一个可运行、可联调、可继续演进的微服务版教务系统里。

---

## 一、当前工程结构

当前 `cloud/` 为 Maven 多模块工程：

- `gateway-service`
- `user-service`
- `education-service`

### `gateway-service`
负责：
- 系统统一入口
- 静态路由转发
- JWT 最小鉴权
- 统一 401 JSON 返回
- 身份上下文透传

### `user-service`
负责：
- 注册与登录
- 用户管理相关能力
- 内部用户校验 / 摘要 / 数量统计
- JWT 发放
- 从 Nacos Config 读取 JWT 与数据库配置

### `education-service`
负责：
- 课程、作业、提交、教师看板等教务域业务
- 本地业务编排
- 通过 Feign 调用 `user-service` 做用户协作补全
- 从 Nacos Config 读取数据库配置

---

## 二、当前已完成模块

## 1. Gateway 与入口层
已完成：
- `/user/**` → `user-service`
- `/education/**` → `education-service`
- `StripPrefix=1`
- 白名单放行：登录 / 注册 / 内部配置相关路径
- 统一鉴权拦截
- 解析 JWT 中的：
  - `userId`
  - `role`
- 透传：
  - `X-User-Id`
  - `X-User-Role`
- 统一 401 JSON 返回

## 2. `user-service`
已完成：
- 注册
- 登录
- 登出占位
- 当前用户信息
- 修改密码
- 学生列表
- 管理员用户列表
- 管理员更新用户
- 内部用户校验：`/internal/users/check`
- 内部用户摘要：`/internal/users/summaries`
- 内部用户数量统计：`/internal/users/count`

## 3. `education-service / course`
已完成：
- 课程列表
- 课程作业数量统计
- 创建课程
- 更新课程
- 删除课程

## 4. `education-service / assignment`
已完成：
- 发布作业
- 管理员 / 教师查看作业列表
- 按课程查看作业列表
- 学生查看待提交作业列表

## 5. `education-service / submission`
已完成：
- 学生提交作业
- 教师 / 管理员查看某作业提交列表
- 教师 / 管理员查看全部提交总览
- 教师批改作业
- 学生查看自己的提交列表
- 学生查看自己的提交详情

## 6. `education-service / teacher dashboard`
已完成：
- 教师端统计概览
- 最近作业
- 最近提交
- Top submitters
- Data screen

---

## 三、当前角色语义

### `admin`
定位为系统管理者 / 教务总负责人：
- 可以看全局数据
- 可以做系统管理与教务管理类动作
- 不承担教师的具体教学执行动作
- 当前不参与批改作业

### `teacher`
定位为教学执行者：
- 发布自己的作业
- 查看自己作业的提交
- 批改自己作业下的提交
- 查看教师端统计信息

### `student`
定位为学习执行者：
- 查看待提交作业
- 提交作业
- 查看自己的提交列表与详情

---

## 四、当前接口边界规范

### 对外接口
- 统一走 Gateway
- 统一返回 `ApiResponse`
- 主要入口前缀：
  - `/user/**`
  - `/education/**`

### 内部服务接口
给 Feign 调用的接口：
- 不强行包一层 `ApiResponse`
- 直接返回轻量业务对象
- 当前包括：
  - `/internal/users/check`
  - `/internal/users/summaries`
  - `/internal/users/count`

### 认证与授权边界
- `Gateway`：负责认证入口、token 校验、身份透传
- `业务服务`：负责业务授权与业务规则判断

也就是说：
- Gateway 解决“你是谁”
- 业务服务解决“你能不能做这件事”

---

## 五、当前配置分层

### 本地配置
各服务 `application.yml` 中保留：
- `server.port`
- `spring.application.name`
- `NACOS_SERVER_ADDR`
- 基础服务配置

### Nacos 共享配置
当前已使用：
- `jwt-config.yaml`
- `db-config.yaml`

其中承接：
- JWT secret 与过期时间
- 数据库 datasource 配置

当前理解为：
- 多服务共用的配置优先进入 Nacos
- 各服务仍保留自己的最小本地启动配置

---

## 六、当前日志规范（第一版）

当前统一日志格式：

```text
%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%logger{36}] [%thread] - %msg%n
```

当前重点日志覆盖：
- 登录链
- Gateway 鉴权链
- assignment 创建 / 查询链
- submission 提交 / 查询 / 批改链
- course 管理与查询链
- teacher dashboard 聚合查询链

当前先不引入：
- `traceId`
- `duration`
- 完整链路追踪体系

---

## 七、当前最核心系统链路

当前最重要的完整链路已经变成：

`Client -> Gateway -> education-service -> user-service -> MySQL`

其中：
- 登录由 `user-service` 提供
- 鉴权由 `gateway-service` 承接
- 教务业务编排由 `education-service` 承接
- 用户身份校验、摘要、数量统计由 `user-service` 提供内部能力

---

## 八、当前工程状态判断

当前 `cloud/` 已经不再只是一个最小骨架，而是一套：
- 已具备基础工程规范
- 已具备角色边界
- 已具备模块级业务链
- 已完成模块级联调验证
- 可以继续承接复杂业务规则和前后端对齐工作的 Spring Cloud 学习版系统

---

## 九、下一阶段重点

接下来更值得做的，不再是继续大面积补同质化接口，而是：

1. 整理已迁模块与待处理复杂模块清单
2. 更新并维护当前 Cloud 文档
3. 推进前后端接口最终对齐
4. 处理复杂业务规则专项
5. 逐步补少量关键链路测试

---

## 十、一句话总结

当前 `cloud/` 的核心价值已经不是“搭起 Spring Cloud 三件套”，而是：
- 已经把 Gateway、Nacos、Feign、JWT、统一返回、配置治理和真实教务业务链真正串成了一套可运行、可联调、可继续演进的微服务学习版系统。
