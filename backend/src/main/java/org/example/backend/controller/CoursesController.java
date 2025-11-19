package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.CreateCoursesRequest;
import org.example.backend.repository.CoursesRepository;
import org.example.backend.service.CoursesService;
import org.example.backend.util.JwtUtil;
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
    public ApiResponse<CoursesResponse> create(@RequestBody CreateCoursesRequest dto) {

        return ApiResponse.success(coursesService.createCourses(dto.getTitle()));
    }

    @GetMapping("/get")
    public ApiResponse<List<CoursesResponse>> get() {
        return ApiResponse.success(coursesService.getAllCourses());
    }
}
