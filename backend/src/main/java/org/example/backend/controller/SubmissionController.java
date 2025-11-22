package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.SubmissionCreateRequest;
import org.example.backend.dto.SubmissionGradeRequest;
import org.example.backend.service.SubmissionService;
import org.example.backend.util.JwtUtil;
import org.example.backend.vo.StudentSubmissionResponse;
import org.example.backend.vo.TeacherSubmissionItemResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (!"student".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        Long studentId = jwtUtil.getUserId(request);
        return ApiResponse.success(submissionService.submit(studentId, req));
    }

    @GetMapping("/my")
    public ApiResponse<?> mySubmission(HttpServletRequest request,
                                       @RequestParam Long assignmentId) {
        if (!"student".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        Long studentId = jwtUtil.getUserId(request);
        return ApiResponse.success(submissionService.getSubmissionByStudent(studentId, assignmentId));
    }

    @GetMapping("/list")
    public ApiResponse<?> listByAssignment(HttpServletRequest request,
                                           @RequestParam Long assignmentId) {
        if (!"teacher".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(submissionService.listByAssignment(assignmentId));
    }

    // 老师查看所有学生提交列表
    @GetMapping("/all")
    public ApiResponse<?> listAll(HttpServletRequest request) {
        if (!"teacher".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        List<TeacherSubmissionItemResponse> list = submissionService.listAllSubmissionsForTeacher();
        return ApiResponse.success(list);
    }

    // 学生查看自己的提交列表
    @GetMapping("/my/list")
    public ApiResponse<?> listByStudent(HttpServletRequest request) {
        if (!"student".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        Long studentId = jwtUtil.getUserId(request);
        return ApiResponse.success(submissionService.listByStudent(studentId));
    }

    @PostMapping("/grade")
    public ApiResponse<?> grade(
            HttpServletRequest request,
            @RequestBody SubmissionGradeRequest req
    ) {
        if (!"teacher".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        submissionService.grade(req);
        return ApiResponse.success("批改成功");
    }
}
