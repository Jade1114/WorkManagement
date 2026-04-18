package org.example.workmanagement.cloud.education.service;

import java.util.List;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.entity.Assignment;
import org.example.workmanagement.cloud.education.mapper.AssignmentMapper;
import org.example.workmanagement.cloud.education.mapper.SubmissionMapper;
import org.example.workmanagement.cloud.education.vo.SubmissionListItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SubmissionQueryService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionQueryService.class);

    private final SubmissionMapper submissionMapper;
    private final AssignmentMapper assignmentMapper;

    public SubmissionQueryService(SubmissionMapper submissionMapper, AssignmentMapper assignmentMapper) {
        this.submissionMapper = submissionMapper;
        this.assignmentMapper = assignmentMapper;
    }

    public List<SubmissionListItemResponse> listByAssignment(Long userId, String userRole, Long assignmentId) {
        log.info("list submissions start: userId={}, role={}, assignmentId={}", userId, userRole, assignmentId);

        if (userId == null) {
            log.warn("list submissions failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("list submissions failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (assignmentId == null) {
            log.warn("list submissions failed: userId={}, role={}, reason=assignmentId missing", userId, userRole);
            throw new BusinessException("assignmentId 不能为空");
        }
        if (!"admin".equals(userRole) && !"teacher".equals(userRole)) {
            log.warn("list submissions failed: userId={}, role={}, reason=role not allowed", userId, userRole);
            throw new BusinessException("当前用户无权限查看提交列表");
        }

        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            log.warn("list submissions failed: userId={}, assignmentId={}, reason=assignment not found", userId,
                    assignmentId);
            throw new BusinessException("作业不存在");
        }
        if ("teacher".equals(userRole) && !userId.equals(assignment.getTeacherId())) {
            log.warn("list submissions failed: userId={}, assignmentId={}, reason=teacher not owner", userId,
                    assignmentId);
            throw new BusinessException("当前用户无权限查看该作业提交列表");
        }

        List<SubmissionListItemResponse> result = submissionMapper
                .selectByAssignmentIdOrderBySubmitTimeDesc(assignmentId).stream()
                .map(submission -> new SubmissionListItemResponse(
                        submission.getId(),
                        submission.getAssignmentId(),
                        submission.getStudentId(),
                        submission.getContent(),
                        submission.getScore(),
                        submission.getComment(),
                        submission.getGraded()))
                .toList();

        log.info("list submissions success: userId={}, role={}, assignmentId={}, count={}",
                userId, userRole, assignmentId, result.size());
        return result;
    }
}
