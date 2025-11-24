-- 初始化 WorkManagement 数据库（首次启动 MySQL 容器时执行）
-- 默认账号：
--  teacher001 / 123456
--  student001 / 123456
--  student002 / 123456

SET NAMES utf8mb4;
-- 可选：更明确一点
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;


USE WorkManagement;

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `course` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `assignment` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NULL,
    deadline DATETIME NULL,
    FOREIGN KEY (course_id) REFERENCES course(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `submission` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    assignment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    score INT NULL,
    comment TEXT NULL,
    graded BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (assignment_id) REFERENCES assignment(id),
    FOREIGN KEY (student_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 示例用户（密码均为 123456 的 BCrypt）
INSERT INTO `user` (username, password, role) VALUES
('teacher001', '$2a$10$RQxpVuZGgnfe5lgXnkc/1ODooBk6I5avnhRqRrnE5mVaRqGBIwXtu', 'teacher'),
('student001', '$2a$10$TXbPiA6ESkp2YZhyWF1lbej5oCV6eRBNZmPXK8BxvGoH0xx733X7O', 'student'),
('student002', '$2a$10$PyvK2LJJFijeFcIOtgIC2.t.sMLGGHYn4dwqkDzAin.pbPV0gl4.S', 'student')
ON DUPLICATE KEY UPDATE
  username = VALUES(username),
  password = VALUES(password),
  role     = VALUES(role);

-- 示例课程
INSERT INTO `course` (id, title) VALUES
(1, 'Java 程序设计'),
(2, '数据库系统概论')
ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 示例作业
INSERT INTO `assignment` (id, course_id, title, content, deadline) VALUES
(1, 1, '第1次作业：变量与数据类型', '完成课后习题1-10', '2024-12-20 23:59:59'),
(2, 1, '第2次作业：流程控制', '完成课堂练习', '2024-12-25 23:59:59'),
(3, 2, '数据库 E-R 模型', '绘制课程管理系统 E-R 图', '2024-12-28 23:59:59')
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content), deadline = VALUES(deadline), course_id = VALUES(course_id);

-- 示例提交
INSERT INTO `submission` (assignment_id, student_id, content, score, comment, graded) VALUES
(1, 2, '完成所有习题，附上代码截图。', 95, '完成得很好！', TRUE),
(1, 3, '部分习题未完成。', NULL, NULL, FALSE),
(2, 2, '流程控制练习代码提交。', NULL, NULL, FALSE)
ON DUPLICATE KEY UPDATE content = VALUES(content), score = VALUES(score), comment = VALUES(comment), graded = VALUES(graded);
