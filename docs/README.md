# WorkManagement 文档索引（2024-新版）

本页汇总仓库内开发文档，按类别分目录，便于快速定位。

## 目录结构
- `api/` — 后端接口文档（auth、user、course、assignment、submission）与小程序登录草案。
- `frontend/` — 前端架构与说明。
- `db/` — 数据库表结构。
- `business/` — 角色与业务规则。
- `design/` — 设计系统与样式规范。
- `plan/` — 历史规划与变更记录。

## 索引
- API：`api/auth_docs.md`，`api/user_docs.md`，`api/course_docs.md`，`api/assignment_docs.md`，`api/submission_docs.md`，`api/wechat-miniprogram-login.md`（小程序接口草案）。
- 前端：`frontend/frontend-overview.md`。
- 数据库：`db/db_docs.md`（与 `docker/mysql/init.sql`、实体同步）。
- 业务：`business/logic.md`。
- 设计：`design/workmanagement-style-en.md`。
- 规划/变更（历史）：`plan/frontend-dev-plan.md`，`plan/update.md`。

## 说明
- 若文档与代码/初始化脚本冲突，以实体类与 `docker/mysql/init.sql` 为准。
- 新文档请在此索引添加入口，并标注“当前/历史”状态。
