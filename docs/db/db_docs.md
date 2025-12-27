# 数据库设计文档（当前版，与 init.sql 同步）

核心业务表 4 张：`user`、`course`、`assignment`、`submission`。密码均为 **BCrypt**。本文件与 `docker/mysql/init.sql`、JPA 实体保持一致。

## 1️⃣ user 表
- 角色：`admin` / `teacher` / `student`（学生可自助注册，教师/管理员由 admin 创建）。
- 禁用：`active`=0 的账号无法登录。

```sql
CREATE TABLE `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  active TINYINT(1) NOT NULL DEFAULT 1,
  openid VARCHAR(64) UNIQUE NULL
);
```

| 字段 | 说明 |
| --- | --- |
| id | 用户 ID |
| username | 学号/工号，唯一 |
| password | BCrypt 密文 |
| role | admin / teacher / student |
| active | 是否启用 |
| openid | 微信 openid（唯一，可空） |

默认账号见 `README.md`。

## 2️⃣ course 表
- 软删除：`deleted` 标识逻辑删除（前端过滤）。

```sql
CREATE TABLE `course` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL UNIQUE,
  deleted TINYINT(1) NOT NULL DEFAULT 0
);
```

| 字段 | 说明 |
| --- | --- |
| id | 课程 ID |
| title | 课程名称，唯一 |
| deleted | 软删除标记 |

学生默认拥有所有课程，不设 student-course 关系表。

## 3️⃣ assignment 表
- 关联 `course_id`；含发布时间 `created_at`，用于排序。

```sql
CREATE TABLE `assignment` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  content TEXT NULL,
  deadline DATETIME NULL,
  created_at DATETIME NULL,
  FOREIGN KEY (course_id) REFERENCES course(id)
);
```

| 字段 | 说明 |
| --- | --- |
| id | 作业 ID |
| course_id | 所属课程 |
| title | 作业标题 |
| content | 作业内容 |
| deadline | 截止时间 |
| created_at | 创建时间 |

## 4️⃣ submission 表
- 记录学生提交，含批改状态与时间。

```sql
CREATE TABLE `submission` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  assignment_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  score INT NULL,
  comment TEXT NULL,
  graded BOOLEAN DEFAULT FALSE,
  submit_time DATETIME NULL,
  FOREIGN KEY (assignment_id) REFERENCES assignment(id),
  FOREIGN KEY (student_id) REFERENCES user(id)
);
```

| 字段 | 说明 |
| --- | --- |
| id | 提交 ID |
| assignment_id | 所属作业 |
| student_id | 提交人 |
| content | 提交内容 |
| score | 评分 |
| comment | 评语 |
| graded | 是否已批改 |
| submit_time | 提交时间 |

## ER 关系（逻辑）
```
user (id, username, role, active)
    ↑
    │ student_id
submission (id, assignment_id, student_id, score, graded, submit_time)
    ↑
    │ assignment_id
assignment (id, course_id, title, deadline, created_at)
    ↑
    │ course_id
course (id, title, deleted)
```

> 若表结构更新，请同步修改本文件与 `docker/mysql/init.sql`，并注明迁移策略。 
