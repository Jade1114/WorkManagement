package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.SubmissionCreateRequest;
import org.example.backend.dto.SubmissionGradeRequest;
import org.example.backend.service.SubmissionService;
import org.example.backend.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Resource
    private SubmissionService submissionService;

    @Resource
    private JwtUtil jwtUtil;

    // 学生提交作业
    @PostMapping("/submit")
    public ApiResponse<?> submit(HttpServletRequest request,
                                 @RequestBody SubmissionCreateRequest req) {
        Long studentId = getUserIdFromToken(request);
        return ApiResponse.success(submissionService.submit(studentId, req));
    }

    @GetMapping("/my")
    public ApiResponse<?> mySubmission(HttpServletRequest request,
                                       @RequestParam Long assignmentId) {
        Long studentId = getUserIdFromToken(request);
        return ApiResponse.success(submissionService.getSubmissionByStudent(studentId, assignmentId));
    }

    @GetMapping("/list")
    public ApiResponse<?> listByAssignment(@RequestParam Long assignmentId) {
        return ApiResponse.success(submissionService.listByAssignment(assignmentId));
    }

    @PostMapping("/grade")
    public ApiResponse<?> grade(
            @RequestBody SubmissionGradeRequest req
    ) {
        submissionService.grade(req);
        return ApiResponse.success("批改成功");
    }



    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUserId(token);
    }
}

