package org.example.workmanagement.cloud.education.controller;

import java.util.List;

import org.example.workmanagement.cloud.education.common.ApiResponse;
import org.example.workmanagement.cloud.education.service.SubmissionQueryService;
import org.example.workmanagement.cloud.education.vo.StudentSubmissionItemResponse;
import org.example.workmanagement.cloud.education.vo.SubmissionListItemResponse;
import org.example.workmanagement.cloud.education.vo.SubmissionResponse;
import org.example.workmanagement.cloud.education.vo.TeacherSubmissionItemResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submissions")
public class SubmissionQueryController {

    private final SubmissionQueryService submissionQueryService;

    public SubmissionQueryController(SubmissionQueryService submissionQueryService) {
        this.submissionQueryService = submissionQueryService;
    }

    @GetMapping
    public ApiResponse<List<SubmissionListItemResponse>> listByAssignment(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam Long assignmentId) {
        return ApiResponse.success(submissionQueryService.listByAssignment(userId, userRole, assignmentId));
    }

    @GetMapping("/list")
    public ApiResponse<List<SubmissionListItemResponse>> listByAssignmentLegacy(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam Long assignmentId) {
        return ApiResponse.success(submissionQueryService.listByAssignment(userId, userRole, assignmentId));
    }

    @GetMapping(value = "/my", params = "!assignmentId")
    public ApiResponse<List<StudentSubmissionItemResponse>> listMySubmissions(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(submissionQueryService.listMySubmissions(userId, userRole));
    }

    @GetMapping("/my/list")
    public ApiResponse<List<StudentSubmissionItemResponse>> listMySubmissionHistory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(submissionQueryService.listMySubmissions(userId, userRole));
    }

    @GetMapping(value = "/my", params = "assignmentId")
    public ApiResponse<SubmissionResponse> getMySubmissionLegacy(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam Long assignmentId) {
        return ApiResponse.success(submissionQueryService.getMySubmission(userId, userRole, assignmentId));
    }

    @GetMapping("/my/detail")
    public ApiResponse<SubmissionResponse> getMySubmission(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam Long assignmentId) {
        return ApiResponse.success(submissionQueryService.getMySubmission(userId, userRole, assignmentId));
    }

    @GetMapping("/all")
    public ApiResponse<List<TeacherSubmissionItemResponse>> listAllSubmissions(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(submissionQueryService.listAllSubmissions(userId, userRole));
    }
}
