package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.CreateCoursesRequest;
import org.example.backend.service.CoursesService;
import org.example.backend.util.JwtUtil;
import org.example.backend.vo.CourseWithCountResponse;
import org.example.backend.vo.CoursesResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CoursesController {
    @Resource
    private CoursesService coursesService;

    @Resource
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public ApiResponse<CoursesResponse> create(HttpServletRequest request,
                                               @RequestBody CreateCoursesRequest dto) {
        if (!"teacher".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(coursesService.createCourses(dto.getTitle()));
    }

    @GetMapping("/get")
    public ApiResponse<List<CoursesResponse>> get() {
        return ApiResponse.success(coursesService.getAllCourses());
    }

    // 教师查看学科及其作业数量
    @GetMapping("/withCount")
    public ApiResponse<List<CourseWithCountResponse>> getWithCount(HttpServletRequest request) {
        if (!"teacher".equals(jwtUtil.getRole(request))) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(coursesService.getCoursesWithAssignmentCount());
    }
}
