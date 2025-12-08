# 📘 Assignment Management System

# 📂 数据库设计文档（最终版）

本系统共包含 4 张核心业务表：

1. `user` —— 用户表（老师 + 学生）
2. `course` —— 课程表
3. `assignment` —— 作业表
4. `submission` —— 提交表（学生提交的作业）

所有密码均使用 **BCrypt 加密**。

---

# 1️⃣ user 表（用户表）

用户角色仅包含：

* `teacher`
* `student`

老师账号由管理员（你）事先插入数据库；学生可自行注册。

## 表结构

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);
```

### 字段说明

| 字段       | 类型      | 说明                |
| -------- | ------- | ----------------- |
| id       | BIGINT  | 用户 ID             |
| username | VARCHAR | 学号 / 工号，唯一        |
| password | VARCHAR | **BCrypt 加密后密码**  |
| role     | VARCHAR | student / teacher |

---

## 示例数据（密码均为 123456 的 BCrypt 加密）

```sql
INSERT INTO user (username, password, role) VALUES
('teacher001', '$2a$10$3CAZ7SyaLHgQ5ZWqjEibVuZUdWvI/UqUTTBWT6I6auL2ptmVGRJJS', 'teacher'),
('20240101', '$2a$10$qXLER9g0JjlRxJmUF/23YOhqRXRLVjCD9Ufk.lXao6HtbT9zUHuPa', 'student'),
('20240102', '$2a$10$cjUx56jg8pX9uAUUfMVXJeyABCqJwD9V9Jr1txkC0kYIKSYmb8xxa', 'student');
```

---

# 2️⃣ course 表（课程表）

课程由老师创建。
学生默认拥有所有课程，不需要 student-course 关系表。

## 表结构

```sql
CREATE TABLE course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL
);
```

### 字段说明

| 字段    | 类型      | 说明    |
| ----- | ------- | ----- |
| id    | BIGINT  | 课程 ID |
| title | VARCHAR | 课程名称  |

---

## 示例数据

```sql
INSERT INTO course (title) VALUES
('Java 程序设计'),
('数据库系统概论');
```

---

# 3️⃣ assignment 表（作业表）

作业属于课程，不需要 teacherId（你的系统中只有一个老师）。

## 表结构

```sql
CREATE TABLE assignment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NULL,
    deadline DATETIME NULL,

    FOREIGN KEY (course_id) REFERENCES course(id)
);
```

### 字段说明

| 字段        | 说明        |
| --------- | --------- |
| id        | 作业 ID     |
| course_id | 所属课程      |
| title     | 作业标题      |
| content   | 作业内容（可为空） |
| deadline  | 截止时间（可为空） |

---

## 示例数据

```sql
INSERT INTO assignment (course_id, title, content, deadline) VALUES
(1, '第1次作业：变量与数据类型', '完成课后习题1-10', '2024-12-20 23:59:59'),
(1, '第2次作业：流程控制', '完成课堂练习', '2024-12-25 23:59:59');
```

---

# 4️⃣ submission 表（提交表）

学生提交的作业记录。

老师可以批改（评分、评语）。

## 表结构

```sql
CREATE TABLE submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    assignment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    score INT NULL,
    comment TEXT NULL,
    graded BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (assignment_id) REFERENCES assignment(id),
    FOREIGN KEY (student_id) REFERENCES user(id)
);
```

### 字段说明

| 字段            | 说明                |
| ------------- | ----------------- |
| assignment_id | 所属作业 ID           |
| student_id    | 提交人               |
| content       | 提交内容              |
| score         | 评分（老师批改后生成）       |
| comment       | 评语                |
| graded        | 是否已批改（true/false） |

---

## 示例数据

```sql
INSERT INTO submission (assignment_id, student_id, content, score, comment, graded)
VALUES
(1, 2, '我的作业内容：完成了所有习题。', 95, '完成得很好！', TRUE),
(1, 3, '我的作业内容：完成部分习题。', NULL, NULL, FALSE),
(2, 2, '流程控制作业内容...', NULL, NULL, FALSE);
```

---

# 📊 五、最终数据库结构图（ER 图逻辑）

```
user (id, username, password, role)
    ↑
    │ student_id
    │
submission (id, assignment_id, student_id, content, score, graded)
    ↑
    │ assignment_id
    │
assignment (id, course_id, title, content, deadline)
    ↑
    │ course_id
    │
course (id, title)
```

结构清晰、简洁、稳定，非常适合课程项目提交。

---