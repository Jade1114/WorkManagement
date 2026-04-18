package org.example.workmanagement.cloud.education.service;

import java.util.List;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.entity.Assignment;
import org.example.workmanagement.cloud.education.mapper.AssignmentMapper;
import org.example.workmanagement.cloud.education.mapper.SubmissionMapper;
import org.example.workmanagement.cloud.education.vo.StudentSubmissionItemResponse;
import org.example.workmanagement.cloud.education.vo.SubmissionListItemResponse;
import org.example.workmanagement.cloud.education.vo.SubmissionResponse;
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

    public List<StudentSubmissionItemResponse> listMySubmissions(Long userId, String userRole) {
        log.info("list my submissions start: userId={}, role={}", userId, userRole);

        if (userId == null) {
            log.warn("list my submissions failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("list my submissions failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (!"student".equals(userRole)) {
            log.warn("list my submissions failed: userId={}, role={}, reason=role not allowed", userId, userRole);
            throw new BusinessException("当前用户无权限查看自己的提交列表");
        }

        List<StudentSubmissionItemResponse> result = submissionMapper.selectByStudentIdOrderBySubmitTimeDesc(userId).stream()
                .map(submission -> new StudentSubmissionItemResponse(
                        submission.getId(),
                        submission.getAssignmentId(),
                        submission.getContent(),
                        submission.getScore(),
                        submission.getComment(),
                        submission.getGraded(),
                        submission.getSubmitTime()))
                .toList();

        log.info("list my submissions success: userId={}, count={}", userId, result.size());
        return result;
    }

    public SubmissionResponse getMySubmission(Long userId, String userRole, Long assignmentId) {
        log.info("get my submission start: userId={}, role={}, assignmentId={}", userId, userRole, assignmentId);

        if (userId == null) {
            log.warn("get my submission failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("get my submission failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (assignmentId == null) {
            log.warn("get my submission failed: userId={}, reason=assignmentId missing", userId);
            throw new BusinessException("assignmentId 不能为空");
        }
        if (!"student".equals(userRole)) {
            log.warn("get my submission failed: userId={}, role={}, reason=role not allowed", userId, userRole);
            throw new BusinessException("当前用户无权限查看自己的提交详情");
        }

        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            log.warn("get my submission failed: userId={}, assignmentId={}, reason=assignment not found", userId,
                    assignmentId);
            throw new BusinessException("作业不存在");
        }

        var submission = submissionMapper.selectByAssignmentIdAndStudentId(assignmentId, userId);
        if (submission == null) {
            log.warn("get my submission failed: userId={}, assignmentId={}, reason=submission not found", userId,
                    assignmentId);
            throw new BusinessException("你还没有提交该作业");
        }

        log.info("get my submission success: userId={}, assignmentId={}, submissionId={}",
                userId, assignmentId, submission.getId());
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
