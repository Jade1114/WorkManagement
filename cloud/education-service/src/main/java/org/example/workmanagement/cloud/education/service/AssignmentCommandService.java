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
import org.springframework.stereotype.Service;

@Service
public class AssignmentCommandService {

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
        if (userId == null) {
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException("当前用户角色缺失");
        }
        if (request.getCourseId() == null) {
            throw new BusinessException("courseId 不能为空");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException("作业标题不能为空");
        }
        if (request.getDeadline() == null) {
            throw new BusinessException("deadline 不能为空");
        }
        if (!"teacher".equals(userRole) && !"admin".equals(userRole)) {
            throw new BusinessException("当前用户无权限发布作业");
        }

        Course course = courseMapper.selectAvailableById(request.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        UserCheckResult userCheckResult = remoteUserCheckService.checkPublisher(userId);
        if (!Boolean.TRUE.equals(userCheckResult.exists())) {
            throw new BusinessException("发布人不存在");
        }
        if (!Boolean.TRUE.equals(userCheckResult.active())) {
            throw new BusinessException("发布人已禁用");
        }
        if (!"teacher".equals(userCheckResult.role()) && !"admin".equals(userCheckResult.role())) {
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
