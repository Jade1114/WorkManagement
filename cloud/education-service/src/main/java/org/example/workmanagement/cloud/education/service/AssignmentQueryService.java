package org.example.workmanagement.cloud.education.service;

import java.util.List;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.entity.Assignment;
import org.example.workmanagement.cloud.education.mapper.AssignmentMapper;
import org.example.workmanagement.cloud.education.vo.AssignmentListItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AssignmentQueryService {

    private static final Logger log = LoggerFactory.getLogger(AssignmentQueryService.class);

    private final AssignmentMapper assignmentMapper;

    public AssignmentQueryService(AssignmentMapper assignmentMapper) {
        this.assignmentMapper = assignmentMapper;
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

        List<AssignmentListItemResponse> result = assignments.stream()
                .map(record -> new AssignmentListItemResponse(
                        record.getId(),
                        record.getCourseId(),
                        record.getTeacherId(),
                        record.getTitle(),
                        record.getContent(),
                        record.getDeadline()))
                .toList();

        log.info("list assignments success: userId={}, role={}, count={}", userId, userRole, result.size());
        return result;
    }
}
