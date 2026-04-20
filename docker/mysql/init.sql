CREATE DATABASE IF NOT EXISTS work_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE work_management;

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `course` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL UNIQUE,
    deleted TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `assignment` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NULL,
    deadline DATETIME NULL,
    created_at DATETIME NULL,
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (teacher_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `submission` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 演示账号
-- admin / admin123
-- teacher001 / teacher123456
-- teacher002 / teacher123456
-- student001 / student123456
-- student002 / student123456
-- student003 / student123456

INSERT INTO `user` (id, username, password, role, active) VALUES
(1, 'admin', '$2a$10$aubLb4odoI4TBwfSnejIP.qMcZMFsA78phSNw9YctAPWCS/9Ulr5W', 'admin', 1),
(2, 'teacher001', '$2a$10$rMy3Bu77vKD/brDbtPKD7OWe8.GzPu.Ln8HJse3C7yIQGE95G8TKy', 'teacher', 1),
(3, 'teacher002', '$2a$10$5cT6lIN5kR0vkrQYxsP2BeJbzoFpR6lmZ3sY3fE2L1.2EGbOOMYm2', 'teacher', 1),
(4, 'student001', '$2a$10$0GRC8.KYaZjj6qHCbfEioO/LbrrimDZ8w5/aMrFJ2aQV6KvXbIdXi', 'student', 1),
(5, 'student002', '$2a$10$FLhopfj5co3pwA2UOa6zYOGSLljfvBaeKYQ0TMTNETigF1269qe9C', 'student', 1),
(6, 'student003', '$2a$10$kUIXZQfLxgYZ1quZnfX1aOlT3DCw5vDXQWCe7yMCq/cEDBlYQa9VW', 'student', 1)
ON DUPLICATE KEY UPDATE
  password = VALUES(password),
  role = VALUES(role),
  active = VALUES(active);

INSERT INTO `course` (id, title, deleted) VALUES
(1, '数据结构与算法', 0),
(2, '操作系统原理', 0),
(3, 'Web 应用开发', 0)
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  deleted = VALUES(deleted);

INSERT INTO `assignment` (id, course_id, teacher_id, title, content, deadline, created_at) VALUES
(1, 1, 2, '链表与栈实现', '完成单链表、栈的增删查操作，提交代码与复杂度分析。', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 1, 2, '排序算法对比', '实现冒泡、快排、归并，并完成性能对比。', DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 2, 3, '进程与线程', '对比进程与线程差异，撰写小结并附示例代码。', DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 3, 3, 'Vue 组件拆分', '基于课程项目拆分并封装通用组件。', DATE_ADD(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY))
ON DUPLICATE KEY UPDATE
  course_id = VALUES(course_id),
  teacher_id = VALUES(teacher_id),
  title = VALUES(title),
  content = VALUES(content),
  deadline = VALUES(deadline),
  created_at = VALUES(created_at);

INSERT INTO `submission` (id, assignment_id, student_id, content, score, comment, graded, submit_time) VALUES
(1, 1, 4, '链表与栈代码提交，包含单元测试。', 92, '实现完整，变量命名清晰。', TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 1, 5, '链表操作完成，栈尚未补充。', NULL, NULL, FALSE, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(3, 2, 4, '排序算法实现，对比结果已记录。', NULL, NULL, FALSE, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(4, 3, 5, '线程与进程小结，附示例代码链接。', 88, '内容清晰，但示例可再补充注释。', TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 4, 6, '拆分了 Header/Footer 组件，并封装了请求工具。', NULL, NULL, FALSE, DATE_SUB(NOW(), INTERVAL 8 HOUR))
ON DUPLICATE KEY UPDATE
  content = VALUES(content),
  score = VALUES(score),
  comment = VALUES(comment),
  graded = VALUES(graded),
  submit_time = VALUES(submit_time);
