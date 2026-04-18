# WorkManagement Cloud

> 这是基于 `WorkManagement` 现有单体项目逐步演进出来的 Spring Cloud 学习版工程。
> 当前目标不是一次性把所有单体功能迁完，而是围绕真实业务链，逐步学习：
> - 服务拆分
> - Nacos 注册发现
> - Gateway 路由与鉴权
> - OpenFeign 服务调用
> - Nacos Config 配置中心
> - 微服务中的身份上下文传递

---

## 一、当前工程结构

当前 `cloud/` 为一个 Maven 多模块工程：

- `gateway-service`
- `user-service`
- `education-service`

### `gateway-service`
负责：
- 系统统一入口
- 显式静态路由
- JWT 最小鉴权入口
- 将认证上下文透传给下游服务

### `user-service`
负责：
- 用户身份最小校验能力
- 最小登录能力
- JWT 发放
- 从 Nacos Config 读取 JWT 配置

### `education-service`
负责：
- 课程 / 作业所属的教务业务
- 发布作业真实业务链
- 本地查课程 + 远程调用户服务

---

## 二、当前已完成的能力

### 1. 服务注册发现
已完成：
- 三个服务注册到 Nacos
- 可以通过服务名完成发现与调用

### 2. Gateway 显式静态路由
当前对外路径：
- `/user/**` → `user-service`
- `/education/**` → `education-service`

并通过 `StripPrefix=1` 去掉网关层前缀，使下游服务接收到自己真正的业务路径。

### 3. OpenFeign 最小调用链
已完成：
- `education-service` 调 `user-service`
- 获取最小用户校验结果：
  - `exists`
  - `role`
  - `active`

### 4. 发布作业真实业务链
已完成：
- `education-service` 本地校验请求参数
- 本地校验课程是否存在
- 远程校验用户是否存在/启用/角色允许
- 插入 `assignment` 数据

### 5. Nacos Config 第一层
已完成：
- 使用共享配置 `jwt-config.yaml`
- `user-service` 和 `gateway-service` 都可从 Nacos Config 读取：
  - `jwt.secret`
  - `jwt.expire-ms`

### 6. 最小登录能力
已完成：
- `POST /user/auth/login`
- 用户名密码校验
- 使用 BCrypt 校验密码
- 使用 JWT 返回 token

### 7. Gateway 最小鉴权
已完成：
- 放行：`/user/auth/login`
- 放行：`/user/internal/config/**`
- 拦截：`/education/**`
- 校验 `Authorization: Bearer ...`
- 解析：
  - `userId`
  - `role`
- 透传给下游：
  - `X-User-Id`
  - `X-User-Role`

### 8. 身份上下文替代显式用户参数
已完成：
- 发布作业请求体中移除 `publisherId`
- `education-service` 改为从 Gateway 透传头中读取：
  - `X-User-Id`
  - `X-User-Role`

### 9. 统一返回与异常处理第一层
已完成：
- `gateway-service`
- `user-service`
- `education-service`

对外接口统一开始采用：
- `code`
- `message`
- `data`

业务服务中已接入：
- `BusinessException`
- `GlobalExceptionHandler`

### 10. 日志第一层
已完成：
- 三个服务统一 `logback-spring.xml`
- 当前统一日志格式：
  - 时间
  - 级别
  - logger
  - 线程
  - 日志正文

并在关键链路补了第一批日志：
- 登录链
- Gateway 鉴权链
- 发布作业链

---

## 三、当前接口边界规范

### 对外接口
对前端 / 系统外部暴露的接口：
- 统一走 Gateway
- 统一采用 `ApiResponse`
- 例如：
  - `/user/auth/login`
  - `/education/assignments/create`

### 内部服务接口
给 Feign 调用的接口：
- 保持轻量业务对象返回
- 当前不强行包一层 `ApiResponse`
- 例如：
  - `/internal/users/check`

### 认证与授权边界
- `Gateway`：负责 token 校验、身份解析、上下文透传
- `业务服务`：负责业务授权和业务规则判断

也就是说：
- Gateway 做统一认证入口
- `education-service` 继续负责“当前角色能否发布作业”这类业务判断

---

## 四、当前配置分层

### 本地配置
当前继续保留在各服务 `application.yml` 中的，主要是：
- `server.port`
- `spring.application.name`
- `NACOS_SERVER_ADDR`
- 数据库基础连接配置

### Nacos 共享配置
当前已抽出：
- `jwt-config.yaml`

其中包含：
- `jwt.secret`
- `jwt.expire-ms`

当前理解为：
- 共享配置源可以被多个服务导入
- 各服务内部依然有自己的 `JwtProperties` 用于绑定配置

---

## 五、当前日志规范（第一版）

当前日志格式：

```text
%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%logger{36}] [%thread] - %msg%n
```

当前优先记录的关键日志：
- `user-service / AuthService`
  - 登录开始 / 登录失败 / 登录成功
- `gateway-service / AuthGlobalFilter`
  - 鉴权放行 / 缺失 token / 无效 token / 鉴权成功
- `education-service / AssignmentCommandService`
  - 创建作业开始 / 失败原因 / 创建成功

当前先不引入：
- `traceId`
- `duration`
- 完整链路追踪体系

---

## 六、当前最重要的系统链路

当前最核心的完整链路已经变成：

`Client -> Gateway -> education-service -> user-service -> MySQL`

其中：
- 登录由 `user-service` 提供
- 鉴权由 `gateway-service` 承接
- 业务编排由 `education-service` 承接
- 用户身份校验由 `user-service` 提供最小能力

---

## 七、后续推进方向

当前建议的后续顺序：

1. 继续补日志第一层
2. 编写并维护统一文档
3. 按业务链逐步迁移单体项目中未迁移的接口和模块
4. 做前后端接口对齐
5. 每迁移一个阶段后做一次结构复盘与小优化

当前不建议：
- 一次性把单体所有功能全部迁完
- 过早细拆更多服务
- 过早引入复杂鉴权体系（refresh token、黑名单、复杂 RBAC）

---

## 八、一句话总结

当前 `cloud/` 已经不是一个空骨架，而是一个围绕真实业务链逐步演进的 Spring Cloud 学习版系统。

当前最核心的价值在于：
- 已经把注册发现、Gateway、Feign、Config、JWT、统一返回、日志和真实业务链串成了一条可运行、可解释、可继续扩展的微服务主线。
