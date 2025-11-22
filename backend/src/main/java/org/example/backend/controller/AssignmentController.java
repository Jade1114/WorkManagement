package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.AssignmentCreateRequest;
import org.example.backend.service.AssignmentService;
import org.example.backend.util.JwtUtil;
import org.example.backend.vo.AssignmentResponse;
import org.example.backend.vo.PendingAssignmentResponse;
import org.example.backend.vo.TeacherAssignmentResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Resource
    private AssignmentService assignmentService;

    @Resource
    private JwtUtil jwtUtil;

    // 创建作业
    @PostMapping("/create")
    public ApiResponse<AssignmentResponse> create(HttpServletRequest request,
            @RequestBody AssignmentCreateRequest req) {
        if (!"teacher".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(assignmentService.createAssignment(req));
    }

    // 获取某课程的所有作业
    @GetMapping("/list")
    public ApiResponse<List<AssignmentResponse>> listByCourse(@RequestParam Long courseId) {
        return ApiResponse.success(assignmentService.getAssignmentsByCourse(courseId));
    }

    // 教师获取所有作业列表
    @GetMapping("/all")
    public ApiResponse<List<TeacherAssignmentResponse>> listAll(HttpServletRequest request) {
        if (!"teacher".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(assignmentService.getAllAssignments());
    }

    // 学生获取自己尚未提交的作业
    @GetMapping("/pending")
    public ApiResponse<List<PendingAssignmentResponse>> pending(HttpServletRequest request) {
        if (!"student".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        Long studentId = jwtUtil.getUserId(request);
        return ApiResponse.success(assignmentService.getUnsubmittedAssignmentsByStudent(studentId));
    }
}
