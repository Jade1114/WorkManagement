package org.example.workmanagement.cloud.education.service;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.dto.CourseCreateRequest;
import org.example.workmanagement.cloud.education.dto.CourseUpdateRequest;
import org.example.workmanagement.cloud.education.entity.Course;
import org.example.workmanagement.cloud.education.mapper.CourseMapper;
import org.example.workmanagement.cloud.education.vo.CourseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CourseCommandService {

    private static final Logger log = LoggerFactory.getLogger(CourseCommandService.class);

    private final CourseMapper courseMapper;

    public CourseCommandService(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    public CourseResponse createCourse(Long userId, String userRole, CourseCreateRequest request) {
        log.info("create course start: userId={}, role={}, title={}", userId, userRole, request.title());
        requireTeacherOrAdmin(userId, userRole, "当前用户无权限创建课程");

        String title = requireTitle(request.title());
        if (courseMapper.countAvailableByTitle(title) > 0) {
            log.warn("create course failed: userId={}, title={}, reason=title exists", userId, title);
            throw new BusinessException("课程已存在");
        }

        Course course = new Course();
        course.setTitle(title);
        course.setDeleted(false);
        courseMapper.insert(course);

        log.info("create course success: userId={}, courseId={}", userId, course.getId());
        return new CourseResponse(course.getId(), course.getTitle());
    }

    public CourseResponse updateCourse(Long userId, String userRole, Long courseId, CourseUpdateRequest request) {
        log.info("update course start: userId={}, role={}, courseId={}", userId, userRole, courseId);
        requireTeacherOrAdmin(userId, userRole, "当前用户无权限更新课程");

        Course course = courseMapper.selectAvailableById(courseId);
        if (course == null) {
            log.warn("update course failed: userId={}, courseId={}, reason=course not found", userId, courseId);
            throw new BusinessException("课程不存在");
        }

        String title = normalizeTitle(request.title());
        if (title == null) {
            log.info("update course skipped: userId={}, courseId={}, reason=title blank", userId, courseId);
            return new CourseResponse(course.getId(), course.getTitle());
        }
        if (courseMapper.countAvailableByTitleExceptId(course.getId(), title) > 0) {
            log.warn("update course failed: userId={}, courseId={}, title={}, reason=title exists",
                    userId, course.getId(), title);
            throw new BusinessException("课程已存在");
        }

        courseMapper.updateTitle(course.getId(), title);
        log.info("update course success: userId={}, courseId={}", userId, course.getId());
        return new CourseResponse(course.getId(), title);
    }

    public void deleteCourse(Long userId, String userRole, Long courseId) {
        log.info("delete course start: userId={}, role={}, courseId={}", userId, userRole, courseId);
        requireTeacherOrAdmin(userId, userRole, "当前用户无权限删除课程");

        if (courseId == null) {
            log.warn("delete course failed: userId={}, reason=courseId missing", userId);
            throw new BusinessException("课程ID不能为空");
        }
        Course course = courseMapper.selectAvailableById(courseId);
        if (course == null) {
            log.warn("delete course failed: userId={}, courseId={}, reason=course not found", userId, courseId);
            throw new BusinessException("课程不存在");
        }

        courseMapper.softDelete(courseId);
        log.info("delete course success: userId={}, courseId={}", userId, courseId);
    }

    private void requireTeacherOrAdmin(Long userId, String userRole, String message) {
        if (userId == null) {
            log.warn("course command permission check failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("course command permission check failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (!"teacher".equals(userRole) && !"admin".equals(userRole)) {
            log.warn("course command permission check failed: userId={}, role={}, reason=role not allowed",
                    userId, userRole);
            throw new BusinessException(message);
        }
    }

    private String requireTitle(String title) {
        String normalized = normalizeTitle(title);
        if (normalized == null) {
            throw new BusinessException("课程标题不能为空");
        }
        return normalized;
    }

    private String normalizeTitle(String title) {
        if (title == null || title.isBlank()) {
            return null;
        }
        return title.trim();
    }
}
