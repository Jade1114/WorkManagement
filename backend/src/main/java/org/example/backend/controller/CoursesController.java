package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.CreateCoursesRequest;
import org.example.backend.dto.UpdateCoursesRequest;
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

    // 教师创建课程
    @PostMapping("/create")
    public ApiResponse<CoursesResponse> create(HttpServletRequest request,
                                               @RequestBody CreateCoursesRequest dto) {
        String role = jwtUtil.getRole(request);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(coursesService.createCourses(dto.getTitle()));
    }

    // 获取所有课程
    @GetMapping("/get")
    public ApiResponse<List<CoursesResponse>> get() {
        return ApiResponse.success(coursesService.getAllCourses());
    }

    // 教师查看学科及其作业数量
    @GetMapping("/withCount")
    public ApiResponse<List<CourseWithCountResponse>> getWithCount(HttpServletRequest request) {
        String role = jwtUtil.getRole(request);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(coursesService.getCoursesWithAssignmentCount());
    }

    // 更新课程名称
    @PutMapping("/update")
    public ApiResponse<CoursesResponse> update(HttpServletRequest request,
                                               @RequestBody UpdateCoursesRequest dto) {
        String role = jwtUtil.getRole(request);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(coursesService.updateCourses(dto));
    }

    // 软删除课程
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> delete(HttpServletRequest request, @PathVariable Long id) {
        String role = jwtUtil.getRole(request);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        coursesService.deleteCourse(id);
        return ApiResponse.success("删除成功");
    }
}
