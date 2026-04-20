package org.example.workmanagement.cloud.education.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.workmanagement.cloud.education.common.BusinessException;
import org.example.workmanagement.cloud.education.dto.TopSubmitterStat;
import org.example.workmanagement.cloud.education.dto.UserSummaryResult;
import org.example.workmanagement.cloud.education.entity.Assignment;
import org.example.workmanagement.cloud.education.entity.Course;
import org.example.workmanagement.cloud.education.entity.Submission;
import org.example.workmanagement.cloud.education.mapper.AssignmentMapper;
import org.example.workmanagement.cloud.education.mapper.CourseMapper;
import org.example.workmanagement.cloud.education.mapper.SubmissionMapper;
import org.example.workmanagement.cloud.education.vo.DataScreenResponse;
import org.example.workmanagement.cloud.education.vo.RecentAssignmentResponse;
import org.example.workmanagement.cloud.education.vo.RecentSubmissionResponse;
import org.example.workmanagement.cloud.education.vo.TeacherStatsResponse;
import org.example.workmanagement.cloud.education.vo.TopSubmitterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TeacherDashboardService {

    private static final Logger log = LoggerFactory.getLogger(TeacherDashboardService.class);

    private final AssignmentMapper assignmentMapper;
    private final SubmissionMapper submissionMapper;
    private final CourseMapper courseMapper;
    private final RemoteUserCheckService remoteUserCheckService;

    public TeacherDashboardService(AssignmentMapper assignmentMapper,
            SubmissionMapper submissionMapper,
            CourseMapper courseMapper,
            RemoteUserCheckService remoteUserCheckService) {
        this.assignmentMapper = assignmentMapper;
        this.submissionMapper = submissionMapper;
        this.courseMapper = courseMapper;
        this.remoteUserCheckService = remoteUserCheckService;
    }

    public TeacherStatsResponse stats(Long userId, String userRole) {
        log.info("teacher stats start: userId={}, role={}", userId, userRole);
        requireTeacherOrAdmin(userId, userRole);

        List<Assignment> assignments = scopedAssignments(userId, userRole);
        List<Long> assignmentIds = assignments.stream().map(Assignment::getId).toList();
        long pendingSubmissions = "admin".equals(userRole)
                ? submissionMapper.countPendingAll()
                : (assignmentIds.isEmpty() ? 0 : submissionMapper.countPendingByAssignmentIds(assignmentIds));
        long assignmentCount = "admin".equals(userRole)
                ? assignmentMapper.countAll()
                : assignmentMapper.countByTeacherId(userId);
        long students = remoteUserCheckService.countUsersByRole("student");
        long courses = courseMapper.countAvailable();

        log.info("teacher stats success: userId={}, pending={}, assignments={}, students={}, courses={}",
                userId, pendingSubmissions, assignmentCount, students, courses);
        return new TeacherStatsResponse(pendingSubmissions, assignmentCount, students, courses);
    }

    public List<RecentAssignmentResponse> recentAssignments(Long userId, String userRole) {
        log.info("recent assignments start: userId={}, role={}", userId, userRole);
        requireTeacherOrAdmin(userId, userRole);

        List<Assignment> assignments = "admin".equals(userRole)
                ? assignmentMapper.selectRecentOrderByCreatedAtDesc(2)
                : assignmentMapper.selectRecentByTeacherIdOrderByCreatedAtDesc(userId, 2);
        Map<Long, String> courseTitleMap = courseTitleMap(assignments.stream()
                .map(Assignment::getCourseId)
                .collect(Collectors.toSet()));

        List<RecentAssignmentResponse> result = assignments.stream()
                .map(assignment -> new RecentAssignmentResponse(
                        assignment.getId(),
                        assignment.getCourseId(),
                        courseTitleMap.getOrDefault(assignment.getCourseId(), "未关联课程"),
                        assignment.getTitle(),
                        assignment.getDeadline(),
                        assignment.getCreatedAt()))
                .toList();

        log.info("recent assignments success: userId={}, count={}", userId, result.size());
        return result;
    }

    public List<RecentSubmissionResponse> recentSubmissions(Long userId, String userRole) {
        log.info("recent submissions start: userId={}, role={}", userId, userRole);
        requireTeacherOrAdmin(userId, userRole);

        List<Assignment> assignments = scopedAssignments(userId, userRole);
        if (!"admin".equals(userRole) && assignments.isEmpty()) {
            return List.of();
        }

        List<Long> scopedAssignmentIds = assignments.stream().map(Assignment::getId).toList();
        List<Submission> submissions = "admin".equals(userRole)
                ? submissionMapper.selectRecentOrderBySubmitTimeDesc(2)
                : submissionMapper.selectRecentByAssignmentIdsOrderBySubmitTimeDesc(scopedAssignmentIds, 2);
        if (submissions.isEmpty()) {
            return List.of();
        }

        Map<Long, Assignment> assignmentMap = assignmentMap(submissions.stream()
                .map(Submission::getAssignmentId)
                .collect(Collectors.toSet()));
        Map<Long, String> courseTitleMap = courseTitleMap(assignmentMap.values().stream()
                .map(Assignment::getCourseId)
                .collect(Collectors.toSet()));
        Map<Long, String> studentNameMap = userNameMap(submissions.stream()
                .map(Submission::getStudentId)
                .collect(Collectors.toSet()));

        List<RecentSubmissionResponse> result = submissions.stream()
                .map(submission -> {
                    Assignment assignment = assignmentMap.get(submission.getAssignmentId());
                    return new RecentSubmissionResponse(
                            submission.getId(),
                            studentNameMap.getOrDefault(submission.getStudentId(), "未知学生"),
                            assignment == null ? "未知作业" : assignment.getTitle(),
                            courseTitle(courseTitleMap, assignment),
                            submission.getGraded(),
                            submission.getScore(),
                            submission.getSubmitTime());
                })
                .toList();

        log.info("recent submissions success: userId={}, count={}", userId, result.size());
        return result;
    }

    public List<TopSubmitterResponse> topSubmitters(Long userId, String userRole, int limit) {
        log.info("top submitters start: userId={}, role={}, limit={}", userId, userRole, limit);
        requireTeacherOrAdmin(userId, userRole);

        int size = limit > 0 ? limit : 3;
        List<Assignment> assignments = scopedAssignments(userId, userRole);
        if (!"admin".equals(userRole) && assignments.isEmpty()) {
            return List.of();
        }

        List<TopSubmitterStat> stats = "admin".equals(userRole)
                ? submissionMapper.selectTopSubmitters(size)
                : submissionMapper.selectTopSubmittersByAssignmentIds(
                        assignments.stream().map(Assignment::getId).toList(), size);
        if (stats.isEmpty()) {
            return List.of();
        }

        Map<Long, String> studentNameMap = userNameMap(stats.stream()
                .map(TopSubmitterStat::getStudentId)
                .collect(Collectors.toSet()));
        List<TopSubmitterResponse> result = stats.stream()
                .map(stat -> new TopSubmitterResponse(
                        stat.getStudentId(),
                        studentNameMap.getOrDefault(stat.getStudentId(), "未知学生"),
                        stat.getCount(),
                        stat.getLastSubmit()))
                .toList();

        log.info("top submitters success: userId={}, count={}", userId, result.size());
        return result;
    }

    public DataScreenResponse dataScreen(Long userId, String userRole) {
        log.info("data screen start: userId={}, role={}", userId, userRole);
        requireTeacherOrAdmin(userId, userRole);

        List<Assignment> assignments = scopedAssignments(userId, userRole);
        Map<Long, Long> assignmentsByCourseId = assignments.stream()
                .collect(Collectors.groupingBy(Assignment::getCourseId, Collectors.counting()));
        List<DataScreenResponse.CourseAssignmentsStat> assignmentsByCourse = courseMapper
                .selectAllAvailableOrderById().stream()
                .map(course -> new DataScreenResponse.CourseAssignmentsStat(
                        course.getTitle(),
                        assignmentsByCourseId.getOrDefault(course.getId(), 0L)))
                .toList();

        List<Submission> submissions;
        if ("admin".equals(userRole)) {
            submissions = submissionMapper.selectAllOrderBySubmitTimeDesc();
        } else {
            List<Long> assignmentIds = assignments.stream().map(Assignment::getId).toList();
            submissions = assignmentIds.isEmpty()
                    ? List.of()
                    : submissionMapper.selectByAssignmentIdsOrderBySubmitTimeDesc(assignmentIds);
        }

        long graded = submissions.stream()
                .filter(submission -> Boolean.TRUE.equals(submission.getGraded()))
                .count();
        long pending = submissions.stream()
                .filter(submission -> !Boolean.TRUE.equals(submission.getGraded()))
                .count();

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);
        LocalDate today = LocalDate.now();
        Map<LocalDate, Long> dailyMap = submissions.stream()
                .filter(submission -> submission.getSubmitTime() != null)
                .filter(submission -> !submission.getSubmitTime().toLocalDate().isBefore(sevenDaysAgo))
                .collect(Collectors.groupingBy(
                        submission -> submission.getSubmitTime().toLocalDate(),
                        Collectors.counting()));
        List<DataScreenResponse.DailySubmissionStat> submissionsByDate = sevenDaysAgo
                .datesUntil(today.plusDays(1))
                .map(date -> new DataScreenResponse.DailySubmissionStat(
                        date.toString(),
                        dailyMap.getOrDefault(date, 0L)))
                .toList();

        log.info("data screen success: userId={}, assignmentsByCourse={}, submissions={}",
                userId, assignmentsByCourse.size(), submissions.size());
        return new DataScreenResponse(
                assignmentsByCourse,
                new DataScreenResponse.SubmissionStatusStat(graded, pending),
                submissionsByDate);
    }

    private void requireTeacherOrAdmin(Long userId, String userRole) {
        if (userId == null) {
            log.warn("teacher dashboard permission check failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("teacher dashboard permission check failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (!"teacher".equals(userRole) && !"admin".equals(userRole)) {
            log.warn("teacher dashboard permission check failed: userId={}, role={}, reason=role not allowed",
                    userId, userRole);
            throw new BusinessException("当前用户无权限查看教师端数据");
        }
    }

    private List<Assignment> scopedAssignments(Long userId, String userRole) {
        return "admin".equals(userRole)
                ? assignmentMapper.selectAllOrderByCreatedAtDesc()
                : assignmentMapper.selectByTeacherIdOrderByCreatedAtDesc(userId);
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

    private String courseTitle(Map<Long, String> courseTitleMap, Assignment assignment) {
        if (assignment == null || assignment.getCourseId() == null) {
            return "未知课程";
        }
        return courseTitleMap.getOrDefault(assignment.getCourseId(), "未关联课程");
    }
}
