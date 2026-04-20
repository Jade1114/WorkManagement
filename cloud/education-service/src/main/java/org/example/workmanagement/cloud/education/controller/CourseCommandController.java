package org.example.workmanagement.cloud.education.controller;

import org.example.workmanagement.cloud.education.common.ApiResponse;
import org.example.workmanagement.cloud.education.dto.CourseCreateRequest;
import org.example.workmanagement.cloud.education.dto.CourseUpdateRequest;
import org.example.workmanagement.cloud.education.service.CourseCommandService;
import org.example.workmanagement.cloud.education.vo.CourseResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/courses")
public class CourseCommandController {

    private final CourseCommandService courseCommandService;

    public CourseCommandController(CourseCommandService courseCommandService) {
        this.courseCommandService = courseCommandService;
    }

    @PostMapping
    public ApiResponse<CourseResponse> createCourse(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody CourseCreateRequest request) {
        return ApiResponse.success(courseCommandService.createCourse(userId, userRole, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CourseResponse> updateCourse(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id,
            @Valid @RequestBody CourseUpdateRequest request) {
        return ApiResponse.success(courseCommandService.updateCourse(userId, userRole, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCourse(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id) {
        courseCommandService.deleteCourse(userId, userRole, id);
        return ApiResponse.success("删除成功");
    }
}
