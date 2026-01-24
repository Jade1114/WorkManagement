# WorkManagement 前端说明

## 技术与运行
- 技术栈：Vite + Vue 3 (Composition API) + Pinia + Vue Router + Element Plus + Axios。
- 目录：`frontend/src` 下按 `views`（业务页面）、`components`（通用组件）、`layouts`（布局）、`stores`（状态）、`net`（请求封装）、`router`（路由）、`style.css`（设计令牌）组织。
- 开发/构建：在 `frontend` 目录 `npm install`，`npm run dev` 本地调试（`vite.config.js` 代理 `/api`→`http://localhost:8080`），`npm run build` 产物用于部署。

## 路由与权限
- 路由守卫（`src/router/index.js`）：基于 `meta.requiresAuth`、`meta.roles`、`meta.guestOnly` 与 Pinia `userStore` 中的 `token/role` 控制访问；未登录跳转 `/login`，越权跳转 `/error`，已登录访问登录/注册会跳回各角色首页。
- 角色首页：`admin/teacher`→`/dashboard`，`student`→`/student/home`。

## 状态与持久化
- Pinia 插件（`src/stores/index.js`）将标记 `persist: true` 的 store 持久化到 `localStorage`，启动时自动还原。
- `userStore`：保存 `token` 和 `user` 对象，提供 `username`/`role` getter（若缺失则解码 JWT payload）；`setLoginInfo`、`logout`。
- `themeStore`：`mode` 为 `light|dark|auto`，切换写入 DOM `class`，结合 `matchMedia` 跟随系统。

## 网络层
- `src/net/index.js` 用 Axios 统一配置：`baseURL /api`、超时 8s、请求拦截附加 `Authorization`。响应根据业务 `code` 统一 Message 提示并处理 401（清理登录并跳登录）、403、500（跳 `/error`）。

## 主题与样式
- 设计令牌集中于 `src/style.css`：间距、色板、阴影、玻璃态卡片、全局字体等；`.theme-dark` 覆盖暗黑变量并在 `App.vue` 里监听系统偏好自动切换。

## 主要页面/模块
- 布局：`layouts/AdminLayout.vue` 提供侧边导航与顶栏，支持角色过滤菜单与主题切换。
- 欢迎：`views/welcome/LoginView.vue`、`RegisterView.vue` 表单校验+登录后按角色跳转。
- 管理员/教师：`views/admin/DashboardView.vue` 仪表盘（ECharts 柱/折/饼）、`UserManageView.vue` 用户角色/状态管理，`CourseManageView.vue` 学科 CRUD。
- 教师：`views/teacher/TeacherAssignmentsView.vue` 仅查看自己发布作业的提交与作业列表，评分弹窗；`TeacherHomeView.vue` 等用于概览。
- 学生：`views/student/StudentHomeView.vue` 未提交作业列表+提交弹窗，`StudentAssignmentsView.vue` 已提交/得分查看。

## 复用组件
- `TableShell` 统一表格壳与头/脚布局，`Pagination` 双向绑定分页，`TeacherAssignmentTable`/`StudentAssignmentTable` 封装列与标签展示。

## 接口约定示例
- 登录：`POST /auth/login` 返回 `{ token, userId, username, role }`；退出调用 `/auth/logout`（失败忽略仍清理本地）。
- 典型列表：课程 `/courses/withCount`，作业 `/assignments/*`，提交 `/submissions/*`；鉴权走 Bearer Token，后端返回形如 `{ code, data, message }`。

## 答辩可强调的点
- 统一权限路由+Pinia 持久化+Axios 拦截的整体安全链路。
- ECharts + Element Plus 搭建的仪表盘与表单/表格复用能力。
- 主题切换与系统偏好适配，设计令牌化便于扩展。
