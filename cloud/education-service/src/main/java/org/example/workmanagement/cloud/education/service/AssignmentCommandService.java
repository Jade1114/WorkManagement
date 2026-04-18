package org.example.workmanagement.cloud.education.service;

import java.time.LocalDateTime;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.dto.AssignmentCreateRequest;
import org.example.workmanagement.cloud.education.dto.UserCheckResult;
import org.example.workmanagement.cloud.education.entity.Assignment;
import org.example.workmanagement.cloud.education.entity.Course;
import org.example.workmanagement.cloud.education.mapper.AssignmentMapper;
import org.example.workmanagement.cloud.education.mapper.CourseMapper;
import org.example.workmanagement.cloud.education.vo.AssignmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AssignmentCommandService {

    private static final Logger log = LoggerFactory.getLogger(AssignmentCommandService.class);

    private final AssignmentMapper assignmentMapper;
    private final CourseMapper courseMapper;
    private final RemoteUserCheckService remoteUserCheckService;

    public AssignmentCommandService(AssignmentMapper assignmentMapper,
            CourseMapper courseMapper,
            RemoteUserCheckService remoteUserCheckService) {
        this.assignmentMapper = assignmentMapper;
        this.courseMapper = courseMapper;
        this.remoteUserCheckService = remoteUserCheckService;
    }

    public AssignmentResponse createAssignment(Long userId, String userRole, AssignmentCreateRequest request) {
        log.info("create assignment start: userId={}, role={}, courseId={}, title={}",
                userId, userRole, request.getCourseId(), request.getTitle());

        if (userId == null) {
            log.warn("create assignment failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("create assignment failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (request.getCourseId() == null) {
            log.warn("create assignment failed: userId={}, reason=courseId missing", userId);
            throw new BusinessException("courseId 不能为空");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            log.warn("create assignment failed: userId={}, courseId={}, reason=title blank", userId, request.getCourseId());
            throw new BusinessException("作业标题不能为空");
        }
        if (request.getDeadline() == null) {
            log.warn("create assignment failed: userId={}, courseId={}, reason=deadline missing", userId, request.getCourseId());
            throw new BusinessException("deadline 不能为空");
        }
        if (!"teacher".equals(userRole) && !"admin".equals(userRole)) {
            log.warn("create assignment failed: userId={}, role={}, reason=role not allowed", userId, userRole);
            throw new BusinessException("当前用户无权限发布作业");
        }

        Course course = courseMapper.selectAvailableById(request.getCourseId());
        if (course == null) {
            log.warn("create assignment failed: userId={}, courseId={}, reason=course not found", userId, request.getCourseId());
            throw new BusinessException("课程不存在");
        }

        UserCheckResult userCheckResult = remoteUserCheckService.checkPublisher(userId);
        if (!Boolean.TRUE.equals(userCheckResult.exists())) {
            log.warn("create assignment failed: userId={}, reason=publisher not found", userId);
            throw new BusinessException("发布人不存在");
        }
        if (!Boolean.TRUE.equals(userCheckResult.active())) {
            log.warn("create assignment failed: userId={}, reason=publisher disabled", userId);
            throw new BusinessException("发布人已禁用");
        }
        if (!"teacher".equals(userCheckResult.role()) && !"admin".equals(userCheckResult.role())) {
            log.warn("create assignment failed: userId={}, role={}, reason=publisher role not allowed", userId, userCheckResult.role());
            throw new BusinessException("发布人无权限发布作业");
        }

        Assignment assignment = new Assignment();
        assignment.setCourseId(request.getCourseId());
        assignment.setTeacherId(userId);
        assignment.setTitle(request.getTitle());
        assignment.setContent(request.getContent());
        assignment.setDeadline(request.getDeadline());
        assignment.setCreatedAt(LocalDateTime.now());
        assignmentMapper.insert(assignment);

        log.info("create assignment success: assignmentId={}, courseId={}, teacherId={}",
                assignment.getId(), assignment.getCourseId(), assignment.getTeacherId());
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getCourseId(),
                assignment.getTeacherId(),
                assignment.getTitle(),
                assignment.getContent(),
                assignment.getDeadline()
        );
    }
}
