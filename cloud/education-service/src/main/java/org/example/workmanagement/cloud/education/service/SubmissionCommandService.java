package org.example.workmanagement.cloud.education.service;

import java.time.LocalDateTime;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.dto.SubmissionCreateRequest;
import org.example.workmanagement.cloud.education.dto.UserCheckResult;
import org.example.workmanagement.cloud.education.entity.Assignment;
import org.example.workmanagement.cloud.education.entity.Submission;
import org.example.workmanagement.cloud.education.mapper.AssignmentMapper;
import org.example.workmanagement.cloud.education.mapper.SubmissionMapper;
import org.example.workmanagement.cloud.education.vo.SubmissionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SubmissionCommandService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionCommandService.class);

    private final SubmissionMapper submissionMapper;
    private final AssignmentMapper assignmentMapper;
    private final RemoteUserCheckService remoteUserCheckService;

    public SubmissionCommandService(SubmissionMapper submissionMapper,
            AssignmentMapper assignmentMapper,
            RemoteUserCheckService remoteUserCheckService) {
        this.submissionMapper = submissionMapper;
        this.assignmentMapper = assignmentMapper;
        this.remoteUserCheckService = remoteUserCheckService;
    }

    public SubmissionResponse submitAssignment(Long userId, String userRole, SubmissionCreateRequest request) {
        log.info("submit assignment start: userId={}, role={}, assignmentId={}", userId, userRole, request.assignmentId());

        if (userId == null) {
            log.warn("submit assignment failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("submit assignment failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (!"student".equals(userRole)) {
            log.warn("submit assignment failed: userId={}, role={}, reason=role not allowed", userId, userRole);
            throw new BusinessException("当前用户无权限提交作业");
        }

        Assignment assignment = assignmentMapper.selectById(request.assignmentId());
        if (assignment == null) {
            log.warn("submit assignment failed: userId={}, assignmentId={}, reason=assignment not found",
                    userId, request.assignmentId());
            throw new BusinessException("作业不存在");
        }

        UserCheckResult userCheckResult = remoteUserCheckService.checkUser(userId);
        if (!Boolean.TRUE.equals(userCheckResult.exists())) {
            log.warn("submit assignment failed: userId={}, reason=student not found", userId);
            throw new BusinessException("提交人不存在");
        }
        if (!Boolean.TRUE.equals(userCheckResult.active())) {
            log.warn("submit assignment failed: userId={}, reason=student disabled", userId);
            throw new BusinessException("提交人已禁用");
        }
        if (!"student".equals(userCheckResult.role())) {
            log.warn("submit assignment failed: userId={}, role={}, reason=student role mismatch",
                    userId, userCheckResult.role());
            throw new BusinessException("当前用户无权限提交作业");
        }

        Submission existed = submissionMapper.selectByAssignmentIdAndStudentId(request.assignmentId(), userId);
        if (existed != null) {
            log.warn("submit assignment failed: userId={}, assignmentId={}, reason=already submitted",
                    userId, request.assignmentId());
            throw new BusinessException("你已经提交过该作业");
        }

        Submission submission = new Submission();
        submission.setAssignmentId(request.assignmentId());
        submission.setStudentId(userId);
        submission.setContent(request.content());
        submission.setScore(null);
        submission.setComment(null);
        submission.setGraded(false);
        submission.setSubmitTime(LocalDateTime.now());
        submissionMapper.insert(submission);

        log.info("submit assignment success: submissionId={}, assignmentId={}, studentId={}",
                submission.getId(), submission.getAssignmentId(), submission.getStudentId());
        return new SubmissionResponse(
                submission.getId(),
                submission.getAssignmentId(),
                submission.getStudentId(),
                submission.getContent(),
                submission.getScore(),
                submission.getComment(),
                submission.getGraded());
    }
}
