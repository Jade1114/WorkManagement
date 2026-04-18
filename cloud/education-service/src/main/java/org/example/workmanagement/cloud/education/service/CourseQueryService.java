package org.example.workmanagement.cloud.education.service;

import java.util.List;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.mapper.AssignmentMapper;
import org.example.workmanagement.cloud.education.mapper.CourseMapper;
import org.example.workmanagement.cloud.education.vo.CourseResponse;
import org.example.workmanagement.cloud.education.vo.CourseWithAssignmentCountResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CourseQueryService {

    private static final Logger log = LoggerFactory.getLogger(CourseQueryService.class);

    private final CourseMapper courseMapper;
    private final AssignmentMapper assignmentMapper;

    public CourseQueryService(CourseMapper courseMapper, AssignmentMapper assignmentMapper) {
        this.courseMapper = courseMapper;
        this.assignmentMapper = assignmentMapper;
    }

    public List<CourseResponse> listCourses(Long userId, String userRole) {
        log.info("list courses start: userId={}, role={}", userId, userRole);
        requireLogin(userId, userRole);

        List<CourseResponse> result = courseMapper.selectAllAvailableOrderById().stream()
                .map(course -> new CourseResponse(course.getId(), course.getTitle()))
                .toList();

        log.info("list courses success: userId={}, count={}", userId, result.size());
        return result;
    }

    public List<CourseWithAssignmentCountResponse> listCoursesWithAssignmentCount(Long userId, String userRole) {
        log.info("list courses with assignment count start: userId={}, role={}", userId, userRole);
        requireTeacherOrAdmin(userId, userRole, "当前用户无权限查看课程作业数量");

        List<CourseWithAssignmentCountResponse> result = courseMapper.selectAllAvailableOrderById().stream()
                .map(course -> new CourseWithAssignmentCountResponse(
                        course.getId(),
                        course.getTitle(),
                        assignmentMapper.countByCourseId(course.getId())))
                .toList();

        log.info("list courses with assignment count success: userId={}, count={}", userId, result.size());
        return result;
    }

    private void requireTeacherOrAdmin(Long userId, String userRole, String message) {
        requireLogin(userId, userRole);
        if (!"teacher".equals(userRole) && !"admin".equals(userRole)) {
            log.warn("course query permission check failed: userId={}, role={}, reason=role not allowed",
                    userId, userRole);
            throw new BusinessException(message);
        }
    }

    private void requireLogin(Long userId, String userRole) {
        if (userId == null) {
            log.warn("course query permission check failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("course query permission check failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
    }
}
