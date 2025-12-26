> 状态：历史前端重构记录（保留溯源），最新功能/接口以代码与 API 文档为准。

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
- 全局样式：`src/style.css` 使用深色蓝灰基调，卡片/模块采用玻璃质感（半透明+blur），圆角更大。
- 布局样式：`src/layouts/AdminLayout.vue` 顶部/侧栏/内容区统一玻璃背景与轻边框，保持分块且悬浮感。
- 根容器：`src/App.vue` 使用全局背景。
- 阴影增强：`src/style.css` 提升阴影层级，卡片统一使用强阴影；布局内容区改为强阴影，模块与背景对比更明显。
- 按钮样式：`src/style.css` 全局按钮调整为胶囊形态（圆角 999px，适度加大内边距）。

## 布局调整
- 禁用页面滚动：`body` 设置 `overflow: hidden`，`App`/`Layout` 占满视口高度，内容区域内部自适应。
