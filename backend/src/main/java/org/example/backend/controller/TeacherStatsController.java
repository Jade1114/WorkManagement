package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.repository.AssignmentRepository;
import org.example.backend.repository.CoursesRepository;
import org.example.backend.repository.SubmissionRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.util.JwtUtil;
import org.example.backend.vo.TeacherStatsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/stats")
    public ApiResponse<TeacherStatsResponse> stats(HttpServletRequest request) {
        if (!"teacher".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }

        long pendingSubmissions = submissionRepository.countByGradedFalseOrGradedIsNull();
        long assignments = assignmentRepository.count();
        long students = userRepository.countByRole("student");
        long courses = coursesRepository.count();

        return ApiResponse.success(new TeacherStatsResponse(
                pendingSubmissions,
                assignments,
                students,
                courses
        ));
    }
}
