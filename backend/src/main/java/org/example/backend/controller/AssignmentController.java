package org.example.backend.controller;

import jakarta.annotation.Resource;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.AssignmentCreateRequest;
import org.example.backend.service.AssignmentService;
import org.example.backend.vo.AssignmentResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Resource
    private AssignmentService assignmentService;

    // 创建作业
    @PostMapping("/create")
    public ApiResponse<AssignmentResponse> create(@RequestBody AssignmentCreateRequest req) {
        return ApiResponse.success(assignmentService.createAssignment(req));
    }

    // 获取某课程的所有作业
    @GetMapping("/list")
    public ApiResponse<List<AssignmentResponse>> listByCourse(@RequestParam Long courseId) {
        return ApiResponse.success(assignmentService.getAssignmentsByCourse(courseId));
    }
}

