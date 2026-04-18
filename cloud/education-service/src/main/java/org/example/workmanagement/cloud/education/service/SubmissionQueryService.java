package org.example.workmanagement.cloud.education.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.dto.UserSummaryResult;
import org.example.workmanagement.cloud.education.entity.Assignment;
import org.example.workmanagement.cloud.education.entity.Course;
import org.example.workmanagement.cloud.education.mapper.AssignmentMapper;
import org.example.workmanagement.cloud.education.mapper.CourseMapper;
import org.example.workmanagement.cloud.education.mapper.SubmissionMapper;
import org.example.workmanagement.cloud.education.vo.StudentSubmissionItemResponse;
import org.example.workmanagement.cloud.education.vo.SubmissionListItemResponse;
import org.example.workmanagement.cloud.education.vo.SubmissionResponse;
import org.example.workmanagement.cloud.education.vo.TeacherSubmissionItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SubmissionQueryService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionQueryService.class);

    private final SubmissionMapper submissionMapper;
    private final AssignmentMapper assignmentMapper;
    private final CourseMapper courseMapper;
    private final RemoteUserCheckService remoteUserCheckService;

    public SubmissionQueryService(SubmissionMapper submissionMapper,
            AssignmentMapper assignmentMapper,
            CourseMapper courseMapper,
            RemoteUserCheckService remoteUserCheckService) {
        this.submissionMapper = submissionMapper;
        this.assignmentMapper = assignmentMapper;
        this.courseMapper = courseMapper;
        this.remoteUserCheckService = remoteUserCheckService;
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

        var submissions = submissionMapper.selectByAssignmentIdOrderBySubmitTimeDesc(assignmentId);
        Map<Long, String> studentNameMap = userNameMap(submissions.stream()
                .map(submission -> submission.getStudentId())
                .collect(Collectors.toSet()));

        List<SubmissionListItemResponse> result = submissions.stream()
                .map(submission -> new SubmissionListItemResponse(
                        submission.getId(),
                        submission.getId(),
                        submission.getAssignmentId(),
                        submission.getStudentId(),
                        studentNameMap.getOrDefault(submission.getStudentId(), "未知学生"),
                        studentNameMap.getOrDefault(submission.getStudentId(), "未知学生"),
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

        var submissions = submissionMapper.selectByStudentIdOrderBySubmitTimeDesc(userId);
        Map<Long, Assignment> assignmentMap = assignmentMap(submissions.stream()
                .map(submission -> submission.getAssignmentId())
                .collect(Collectors.toSet()));
        Map<Long, String> courseTitleMap = courseTitleMap(assignmentMap.values().stream()
                .map(Assignment::getCourseId)
                .collect(Collectors.toSet()));

        List<StudentSubmissionItemResponse> result = submissions.stream()
                .map(submission -> new StudentSubmissionItemResponse(
                        submission.getId(),
                        submission.getAssignmentId(),
                        assignmentTitle(assignmentMap.get(submission.getAssignmentId())),
                        courseId(assignmentMap.get(submission.getAssignmentId())),
                        courseTitle(courseTitleMap, assignmentMap.get(submission.getAssignmentId())),
                        submission.getContent(),
                        submission.getContent(),
                        submission.getComment(),
                        submission.getGraded(),
                        submission.getScore(),
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

    public List<TeacherSubmissionItemResponse> listAllSubmissions(Long userId, String userRole) {
        log.info("list all submissions start: userId={}, role={}", userId, userRole);

        if (userId == null) {
            log.warn("list all submissions failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("list all submissions failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (!"admin".equals(userRole) && !"teacher".equals(userRole)) {
            log.warn("list all submissions failed: userId={}, role={}, reason=role not allowed", userId, userRole);
            throw new BusinessException("当前用户无权限查看提交列表");
        }

        List<Assignment> assignments = "admin".equals(userRole)
                ? assignmentMapper.selectAllOrderByCreatedAtDesc()
                : assignmentMapper.selectByTeacherIdOrderByCreatedAtDesc(userId);
        if (assignments.isEmpty()) {
            return List.of();
        }

        Map<Long, Assignment> assignmentMap = assignments.stream()
                .collect(Collectors.toMap(Assignment::getId, assignment -> assignment, (left, right) -> left));
        List<Long> assignmentIds = assignments.stream().map(Assignment::getId).toList();
        var submissions = "admin".equals(userRole)
                ? submissionMapper.selectAllOrderBySubmitTimeDesc()
                : submissionMapper.selectByAssignmentIdsOrderBySubmitTimeDesc(assignmentIds);
        if (submissions.isEmpty()) {
            return List.of();
        }

        Map<Long, String> courseTitleMap = courseTitleMap(assignments.stream()
                .map(Assignment::getCourseId)
                .collect(Collectors.toSet()));
        Map<Long, String> studentNameMap = userNameMap(submissions.stream()
                .map(submission -> submission.getStudentId())
                .collect(Collectors.toSet()));

        List<TeacherSubmissionItemResponse> result = submissions.stream()
                .map(submission -> {
                    Assignment assignment = assignmentMap.get(submission.getAssignmentId());
                    return new TeacherSubmissionItemResponse(
                            submission.getId(),
                            submission.getAssignmentId(),
                            assignmentTitle(assignment),
                            courseId(assignment),
                            courseTitle(courseTitleMap, assignment),
                            submission.getStudentId(),
                            studentNameMap.getOrDefault(submission.getStudentId(), "未知学生"),
                            assignment == null ? null : assignment.getContent(),
                            submission.getContent(),
                            submission.getSubmitTime() == null ? null : submission.getSubmitTime().toString(),
                            submission.getGraded(),
                            submission.getScore(),
                            submission.getComment());
                })
                .toList();

        log.info("list all submissions success: userId={}, role={}, count={}", userId, userRole, result.size());
        return result;
    }

    private Map<Long, Assignment> assignmentMap(Set<Long> assignmentIds) {
        if (assignmentIds.isEmpty()) {
            return Map.of();
        }
        return assignmentMapper.selectByIds(assignmentIds.stream().toList()).stream()
                .collect(Collectors.toMap(Assignment::getId, assignment -> assignment, (left, right) -> left));
    }

    private Map<Long, String> courseTitleMap(Set<Long> courseIds) {
        if (courseIds.isEmpty()) {
            return Map.of();
        }
        return courseMapper.selectAvailableByIds(courseIds.stream().toList()).stream()
                .collect(Collectors.toMap(Course::getId, Course::getTitle, (left, right) -> left));
    }

    private Map<Long, String> userNameMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return remoteUserCheckService.listUsers(userIds.stream().toList()).stream()
                .collect(Collectors.toMap(UserSummaryResult::id, UserSummaryResult::username, (left, right) -> left));
    }

    private String assignmentTitle(Assignment assignment) {
        return assignment == null ? "未知作业" : assignment.getTitle();
    }

    private Long courseId(Assignment assignment) {
        return assignment == null ? null : assignment.getCourseId();
    }

    private String courseTitle(Map<Long, String> courseTitleMap, Assignment assignment) {
        Long courseId = courseId(assignment);
        if (courseId == null) {
            return "未知课程";
        }
        return courseTitleMap.getOrDefault(courseId, "未关联课程");
    }
}
