## 近期更改摘要

- 路由重构：新增 AdminLayout 作为统一壳，子路由覆盖仪表盘、数据大屏、用户管理、权限管理、学科管理、作业管理、学生端页面；路由守卫支持 admin/teacher/student 角色并按角色过滤访问。
- 登录跳转调整：admin/teacher 登录后进入 `/dashboard`，student 进入 `/student/home`，未知角色跳转错误页。
- 新增页面：
  - `views/admin/DashboardView.vue`：统计卡片 + 最近作业/提交表格。
  - `views/admin/DataScreenView.vue`：数据大屏占位，可跳转外链。
  - `views/admin/UserManageView.vue`：用户角色与状态管理的前端示例数据。
  - `views/admin/PermissionManageView.vue`：角色可访问模块编辑（示例数据）。
  - `views/admin/CourseManageView.vue`：学科列表/创建，复用课程接口。
- 布局导航：`layouts/AdminLayout.vue` 提供顶部栏与侧栏菜单，按角色显示项；提供用户下拉和退出。
- 教师/学生页面适配：移除旧的内页导航，Teacher/Student 页面在统一壳内显示；教师首页与导航目标同步到新的作业管理、课程管理路径。

## 新增功能模块
- 权限管理：`views/admin/PermissionManageView.vue`，基于示例数据编辑角色可访问模块。

## 调整说明
- 去除商品/订单/轮播图相关菜单与页面，聚焦作业管理系统核心功能（用户、权限、学科、作业、学生端、数据大屏）。

## UI 与主题美化
- 全局样式：`src/style.css` 使用 slate/indigo 色板，统一浅色背景，增大圆角，卡片改为实色背景+边框（去掉玻璃态）。
- 布局样式：`src/layouts/AdminLayout.vue` 去除渐变，顶部/侧栏/内容区改为统一实色底板+边框，保持清晰分块。
- 根容器：`src/App.vue` 使用全局浅色背景。
- 阴影增强：`src/style.css` 提升阴影层级，卡片统一使用强阴影；布局内容区改为强阴影，模块与背景对比更明显。
- 色调调整：`src/style.css` 将页面与卡片背景改为带轻微蓝灰的底色，文字对比减轻，避免满屏纯白带来的疲劳感。

## 布局调整
- 禁用页面滚动：`body` 设置 `overflow: hidden`，`App`/`Layout` 占满视口高度，内容区域内部自适应。
