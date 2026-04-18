package org.example.workmanagement.cloud.education.controller;

import java.util.List;

import org.example.workmanagement.cloud.education.common.ApiResponse;
import org.example.workmanagement.cloud.education.service.CourseQueryService;
import org.example.workmanagement.cloud.education.vo.CourseResponse;
import org.example.workmanagement.cloud.education.vo.CourseWithAssignmentCountResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
public class CourseQueryController {

    private final CourseQueryService courseQueryService;

    public CourseQueryController(CourseQueryService courseQueryService) {
        this.courseQueryService = courseQueryService;
    }

    @GetMapping({"", "/get"})
    public ApiResponse<List<CourseResponse>> listCourses(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(courseQueryService.listCourses(userId, userRole));
    }

    @GetMapping("/withCount")
    public ApiResponse<List<CourseWithAssignmentCountResponse>> listCoursesWithAssignmentCount(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(courseQueryService.listCoursesWithAssignmentCount(userId, userRole));
    }
}
