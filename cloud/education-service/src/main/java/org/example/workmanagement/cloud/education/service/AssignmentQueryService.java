package org.example.workmanagement.cloud.education.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.entity.Assignment;
import org.example.workmanagement.cloud.education.entity.Course;
import org.example.workmanagement.cloud.education.mapper.AssignmentMapper;
import org.example.workmanagement.cloud.education.mapper.CourseMapper;
import org.example.workmanagement.cloud.education.mapper.SubmissionMapper;
import org.example.workmanagement.cloud.education.vo.AssignmentListItemResponse;
import org.example.workmanagement.cloud.education.vo.PendingAssignmentItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AssignmentQueryService {

    private static final Logger log = LoggerFactory.getLogger(AssignmentQueryService.class);

    private final AssignmentMapper assignmentMapper;
    private final SubmissionMapper submissionMapper;
    private final CourseMapper courseMapper;

    public AssignmentQueryService(AssignmentMapper assignmentMapper,
            SubmissionMapper submissionMapper,
            CourseMapper courseMapper) {
        this.assignmentMapper = assignmentMapper;
        this.submissionMapper = submissionMapper;
        this.courseMapper = courseMapper;
    }

    public List<AssignmentListItemResponse> listAssignments(Long userId, String userRole) {
        log.info("list assignments start: userId={}, role={}", userId, userRole);

        if (userId == null) {
            log.warn("list assignments failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("list assignments failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }

        List<Assignment> assignments;
        if ("admin".equals(userRole)) {
            assignments = assignmentMapper.selectAllOrderByCreatedAtDesc();
        } else if ("teacher".equals(userRole)) {
            assignments = assignmentMapper.selectByTeacherIdOrderByCreatedAtDesc(userId);
        } else {
            log.warn("list assignments failed: userId={}, role={}, reason=role not allowed", userId, userRole);
            throw new BusinessException("当前用户无权限查看作业列表");
        }

        Map<Long, String> courseTitleMap = courseTitleMap(assignments);
        List<AssignmentListItemResponse> result = assignments.stream()
                .map(record -> new AssignmentListItemResponse(
                        record.getId(),
                        record.getCourseId(),
                        courseTitleMap.getOrDefault(record.getCourseId(), "未关联课程"),
                        record.getTeacherId(),
                        record.getTitle(),
                        record.getContent(),
                        record.getDeadline()))
                .toList();

        log.info("list assignments success: userId={}, role={}, count={}", userId, userRole, result.size());
        return result;
    }

    public List<AssignmentListItemResponse> listAssignmentsByCourse(Long userId, String userRole, Long courseId) {
        log.info("list assignments by course start: userId={}, role={}, courseId={}", userId, userRole, courseId);

        if (userId == null) {
            log.warn("list assignments by course failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("list assignments by course failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (!"student".equals(userRole) && !"teacher".equals(userRole) && !"admin".equals(userRole)) {
            log.warn("list assignments by course failed: userId={}, role={}, reason=role not allowed", userId, userRole);
            throw new BusinessException("当前用户无权限查看课程作业列表");
        }
        if (courseId == null) {
            log.warn("list assignments by course failed: userId={}, reason=courseId missing", userId);
            throw new BusinessException("courseId 不能为空");
        }
        Course course = courseMapper.selectAvailableById(courseId);
        if (course == null) {
            log.warn("list assignments by course failed: userId={}, courseId={}, reason=course not found",
                    userId, courseId);
            throw new BusinessException("课程不存在");
        }

        List<AssignmentListItemResponse> result = assignmentMapper.selectByCourseIdOrderByCreatedAtDesc(courseId).stream()
                .map(record -> new AssignmentListItemResponse(
                        record.getId(),
                        record.getCourseId(),
                        course.getTitle(),
                        record.getTeacherId(),
                        record.getTitle(),
                        record.getContent(),
                        record.getDeadline()))
                .toList();

        log.info("list assignments by course success: userId={}, courseId={}, count={}",
                userId, courseId, result.size());
        return result;
    }

    public List<PendingAssignmentItemResponse> listPendingAssignments(Long userId, String userRole) {
        log.info("list pending assignments start: userId={}, role={}", userId, userRole);

        if (userId == null) {
            log.warn("list pending assignments failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("list pending assignments failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (!"student".equals(userRole)) {
            log.warn("list pending assignments failed: userId={}, role={}, reason=role not allowed", userId, userRole);
            throw new BusinessException("当前用户无权限查看待提交作业列表");
        }

        Set<Long> submittedAssignmentIds = submissionMapper.selectByStudentIdOrderBySubmitTimeDesc(userId).stream()
                .map(submission -> submission.getAssignmentId())
                .collect(Collectors.toSet());

        List<Assignment> pendingAssignments = assignmentMapper.selectAllOrderByCreatedAtDesc().stream()
                .filter(assignment -> !submittedAssignmentIds.contains(assignment.getId()))
                .sorted(Comparator.comparing(Assignment::getDeadline, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        Map<Long, String> courseTitleMap = courseTitleMap(pendingAssignments);

        List<PendingAssignmentItemResponse> result = pendingAssignments.stream()
                .map(assignment -> new PendingAssignmentItemResponse(
                        assignment.getId(),
                        assignment.getCourseId(),
                        courseTitleMap.getOrDefault(assignment.getCourseId(), "未关联课程"),
                        assignment.getTitle(),
                        assignment.getContent(),
                        assignment.getDeadline()))
                .toList();

        log.info("list pending assignments success: userId={}, count={}", userId, result.size());
        return result;
    }

    private Map<Long, String> courseTitleMap(List<Assignment> assignments) {
        Set<Long> courseIds = assignments.stream()
                .map(Assignment::getCourseId)
                .collect(Collectors.toSet());
        if (courseIds.isEmpty()) {
            return Map.of();
        }
        return courseMapper.selectAvailableByIds(courseIds.stream().toList()).stream()
                .collect(Collectors.toMap(Course::getId, Course::getTitle, (left, right) -> left));
    }
}
