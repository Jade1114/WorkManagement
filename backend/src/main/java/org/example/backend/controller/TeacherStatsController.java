package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.repository.AssignmentRepository;
import org.example.backend.repository.CoursesRepository;
import org.example.backend.repository.SubmissionRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.AssignmentService;
import org.example.backend.service.SubmissionService;
import org.example.backend.util.JwtUtil;
import org.example.backend.util.TokenResolver;
import org.example.backend.vo.DataScreenResponse;
import org.example.backend.vo.RecentAssignmentResponse;
import org.example.backend.vo.RecentSubmissionResponse;
import org.example.backend.vo.TeacherStatsResponse;
import org.example.backend.vo.TopSubmitterResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/teacher")
public class TeacherStatsController {

    @Resource
    private SubmissionRepository submissionRepository;

    @Resource
    private AssignmentRepository assignmentRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private CoursesRepository coursesRepository;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private AssignmentService assignmentService;

    @Resource
    private SubmissionService submissionService;

    @Resource
    private TokenResolver resolver;

    // 获取老师管理界面的所有统计数据
    @GetMapping("/stats")
    public ApiResponse<TeacherStatsResponse> stats(HttpServletRequest request) {
        String token = resolver.resolveToken(request);
        String role = jwtUtil.getRole(token);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }

        long pendingSubmissions = submissionRepository.countByGradedFalseOrGradedIsNull();
        long assignments = assignmentRepository.count();
        long students = userRepository.countByRole("student");
        long courses = coursesRepository.countByDeletedFalse();

        return ApiResponse.success(new TeacherStatsResponse(
                pendingSubmissions,
                assignments,
                students,
                courses
        ));
    }

    // 最近发布的作业（按创建时间倒序取前2条）
    @GetMapping("/recent/assignments")
    public ApiResponse<?> recentAssignments(HttpServletRequest request) {
        String token = resolver.resolveToken(request);
        String role = jwtUtil.getRole(token);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        List<RecentAssignmentResponse> list = assignmentService.getRecentAssignments();
        return ApiResponse.success(list);
    }

    // 最近的提交（按提交时间倒序取前2条）
    @GetMapping("/recent/submissions")
    public ApiResponse<?> recentSubmissions(HttpServletRequest request) {
        String token = resolver.resolveToken(request);
        String role = jwtUtil.getRole(token);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        List<RecentSubmissionResponse> list = submissionService.listRecentSubmissions();
        return ApiResponse.success(list);
    }

    @GetMapping("/topSubmitters")
    public ApiResponse<List<TopSubmitterResponse>> topSubmitters(HttpServletRequest request) {
        String token = resolver.resolveToken(request);
        String role = jwtUtil.getRole(token);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        List<TopSubmitterResponse> list = submissionService.listTopSubmitters(3);
        return ApiResponse.success(list);
    }

    // 数据大屏数据
    @GetMapping("/dataScreen")
    public ApiResponse<DataScreenResponse> dataScreen(HttpServletRequest request) {
        String token = resolver.resolveToken(request);
        String role = jwtUtil.getRole(token);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }

        // 每个课程的作业数
        List<DataScreenResponse.CourseAssignmentsStat> assignmentsByCourse = coursesRepository.findAll()
                .stream()
                .map(c -> new DataScreenResponse.CourseAssignmentsStat(
                        c.getTitle(),
                        assignmentRepository.countByCourseId(c.getId())
                ))
                .collect(Collectors.toList());

        // 提交数据缓存
        var submissions = submissionRepository.findAll();

        // 提交状态
        long graded = submissions.stream()
                .filter(s -> Boolean.TRUE.equals(s.getGraded()))
                .count();
        long pending = submissions.stream()
                .filter(s -> !Boolean.TRUE.equals(s.getGraded()))
                .count();
        DataScreenResponse.SubmissionStatusStat submissionStatus =
                new DataScreenResponse.SubmissionStatusStat(graded, pending);

        // 最近7天提交量
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);
        LocalDate today = LocalDate.now();
        Map<LocalDate, Long> dailyMap = submissions.stream()
                .filter(s -> s.getSubmitTime() != null)
                .filter(s -> !s.getSubmitTime().toLocalDate().isBefore(sevenDaysAgo))
                .collect(Collectors.groupingBy(
                        s -> s.getSubmitTime().toLocalDate(),
                        Collectors.counting()
                ));

        List<DataScreenResponse.DailySubmissionStat> submissionsByDate = sevenDaysAgo
                .datesUntil(today.plusDays(1))
                .map(date -> new DataScreenResponse.DailySubmissionStat(
                        date.toString(),
                        dailyMap.getOrDefault(date, 0L)
                ))
                .collect(Collectors.toList());

        return ApiResponse.success(
                new DataScreenResponse(assignmentsByCourse, submissionStatus, submissionsByDate)
        );
    }
}
