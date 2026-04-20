# WorkManagement Cloud

`cloud/` 是 WorkManagement 的微服务后端部分，也是整个项目用于学习 Spring Cloud 的核心载体。

它并不是为了“把单体机械地拆成几个服务”，而是希望借助一个真实业务场景，把以下问题真正落到代码与运行链路里：
- 服务边界如何划分
- Gateway 在项目里到底承担什么职责
- Feign 如何组织服务协作
- Nacos 的注册发现与配置中心分别解决什么问题
- 认证、授权与业务权限应该如何分层
- 一个微服务系统如何从“本地能跑”走向“可复现、可编排、可继续演进”

---

## 1. 工程结构

当前 `cloud/` 为 Maven 多模块工程：

- `gateway-service`
- `user-service`
- `education-service`

父工程位于：
- `cloud/pom.xml`

---

## 2. 服务职责

### `gateway-service`
负责系统统一入口：
- 对外暴露 `/user/**` 与 `/education/**`
- 校验 JWT
- 白名单放行登录 / 注册等请求
- 从 token 中提取 `userId` 与 `role`
- 通过请求头透传身份上下文
- 统一返回 401 JSON

它解决的问题不是业务处理，而是：
- 统一入口
- 统一认证前置
- 隐藏内部服务拓扑

### `user-service`
负责用户与认证域：
- 注册 / 登录 / 登出
- 当前用户信息
- 修改密码
- 管理员用户管理
- JWT 签发
- 内部用户校验、用户摘要、角色人数统计

它是用户数据的主服务，拥有：
- 用户身份
- 用户角色
- 用户启用状态
- JWT 签发职责

### `education-service`
负责教务业务域：
- 课程管理
- 作业管理
- 提交记录
- 教师统计看板

它通过 Feign 与 `user-service` 协作，获取：
- 用户是否存在
- 用户角色
- 用户启用状态
- 用户摘要信息
- 某角色人数统计

---

## 3. 当前已实现模块

### Gateway 与入口层
已完成：
- `/user/**` 路由到 `user-service`
- `/education/**` 路由到 `education-service`
- 统一 JWT 校验
- 统一身份透传
- 统一 401 返回

### User Domain
已完成：
- 注册
- 登录
- 登出占位
- 当前用户信息
- 修改密码
- 学生列表
- 管理员用户列表
- 管理员更新用户
- 内部接口：用户校验 / 用户摘要 / 人数统计

### Education Domain
已完成：
- 课程列表、课程统计、创建、更新、删除
- 作业发布、作业列表、按课程查询作业、待提交作业查询
- 学生提交作业
- 教师 / 管理员查看提交列表与总览
- 教师批改作业
- 学生查看自己的提交记录与提交详情
- 教师统计概览、最近作业、最近提交、Top submitters、数据看板

---

## 4. 架构理解

### 为什么拆成这三个服务
当前拆分不是为了盲目“上微服务”，而是为了在真实业务中练习边界：
- `Gateway`：统一入口与认证前置
- `User Service`：身份与用户域
- `Education Service`：教务业务域

这套拆法的重点是：
- 按职责拆，而不是按 controller 数量拆
- 按业务主权拆，而不是按表机械拆

### 认证与授权边界
当前系统采用的边界是：
- `user-service` 负责登录认证与 JWT 签发
- `gateway-service` 负责请求入口处的 JWT 校验与身份透传
- 业务服务负责真正的授权判断

也就是说：
- Gateway 解决“你是谁”
- Service 解决“你能不能操作这条业务资源”

### 用户数据为什么不放在 `education-service`
因为用户数据的业务主权属于 `user-service`。
`education-service` 只获取自己需要的协作能力，而不维护完整用户模型，这样可以避免：
- 职责重复
- 数据冗余
- 一致性问题
- 模型演进时的多处修改

---

## 5. 当前接口边界

### 对外接口
统一通过 Gateway 暴露：
- `/user/**`
- `/education/**`

统一响应结构：
- `ApiResponse`

### 内部服务接口
内部 Feign 协作接口不再强行包一层 `ApiResponse`，而是直接返回轻量对象。
当前内部接口主要包括：
- `/internal/users/check`
- `/internal/users/summaries`
- `/internal/users/count`

---

## 6. 当前配置分层

### 本地配置
各服务本地 `application.yml` 保留最小启动配置：
- `server.port`
- `spring.application.name`
- `NACOS_SERVER_ADDR`

### Nacos 共享配置
当前共享配置包括：
- `jwt-config.yaml`
- `db-config.yaml`

它们分别负责：
- JWT secret 与过期时间
- 数据库连接信息

这意味着：
- Discovery 解决“服务在哪”
- Config 解决“服务怎么配”

---

## 7. 当前最核心的调用链路

典型业务链路如下：

```text
Client -> Gateway -> education-service -> user-service -> MySQL
```

其中：
- 登录由 `user-service` 提供
- 请求鉴权由 `gateway-service` 承接
- 教务业务由 `education-service` 承接
- 用户协作能力由 `user-service` 提供

---

## 8. 当前项目的学习价值

这个微服务工程当前已经比较完整地体现了 Spring Cloud 第一阶段最核心的内容：
- 服务拆分
- Gateway 统一入口
- Feign 服务协作
- Nacos 注册发现
- Nacos 配置治理
- JWT 认证链路
- 前后端接口对齐
- Docker 化运行链路

它当前更像是一套：
- 可运行
- 可联调
- 可继续演进
- 可用于复盘与简历表达

的 Spring Cloud 学习型项目。

---

## 9. 当前还未完全体现的内容

如果从“更完整的微服务能力”看，当前项目后续还可以继续补：
- 服务治理（超时、降级、幂等、重试）
- 可观测性（traceId、调用链、统一日志）
- 数据一致性与读模型策略
- 更系统的工程化与运行治理

也就是说，当前 `cloud/` 已经完成了：
- **微服务架构拆分阶段**

后续可以继续进入：
- **微服务治理与观测阶段**

---

## 10. 一句话总结

`cloud/` 的核心价值已经不是“把 Spring Cloud 组件搭起来”，而是：

> 基于真实教务业务，把 Gateway、Feign、Nacos、JWT、服务边界和前后端联调真正串成了一套可运行、可复盘、可继续深入学习的微服务系统。
