package org.example.workmanagement.cloud.education.controller;

import java.util.List;

import org.example.workmanagement.cloud.education.common.ApiResponse;
import org.example.workmanagement.cloud.education.service.TeacherDashboardService;
import org.example.workmanagement.cloud.education.vo.DataScreenResponse;
import org.example.workmanagement.cloud.education.vo.RecentAssignmentResponse;
import org.example.workmanagement.cloud.education.vo.RecentSubmissionResponse;
import org.example.workmanagement.cloud.education.vo.TeacherStatsResponse;
import org.example.workmanagement.cloud.education.vo.TopSubmitterResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teachers/me")
public class TeacherDashboardController {

    private final TeacherDashboardService teacherDashboardService;

    public TeacherDashboardController(TeacherDashboardService teacherDashboardService) {
        this.teacherDashboardService = teacherDashboardService;
    }

    @GetMapping("/stats")
    public ApiResponse<TeacherStatsResponse> stats(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(teacherDashboardService.stats(userId, userRole));
    }

    @GetMapping("/recent/assignments")
    public ApiResponse<List<RecentAssignmentResponse>> recentAssignments(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(teacherDashboardService.recentAssignments(userId, userRole));
    }

    @GetMapping("/recent/submissions")
    public ApiResponse<List<RecentSubmissionResponse>> recentSubmissions(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(teacherDashboardService.recentSubmissions(userId, userRole));
    }

    @GetMapping("/top-submitters")
    public ApiResponse<List<TopSubmitterResponse>> topSubmitters(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(teacherDashboardService.topSubmitters(userId, userRole, 3));
    }

    @GetMapping("/data-screen")
    public ApiResponse<DataScreenResponse> dataScreen(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(teacherDashboardService.dataScreen(userId, userRole));
    }
}
