package org.example.workmanagement.cloud.education.controller;

import org.example.workmanagement.cloud.education.common.ApiResponse;
import org.example.workmanagement.cloud.education.dto.SubmissionCreateRequest;
import org.example.workmanagement.cloud.education.dto.SubmissionGradeRequest;
import org.example.workmanagement.cloud.education.service.SubmissionCommandService;
import org.example.workmanagement.cloud.education.vo.SubmissionResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/submissions")
public class SubmissionCommandController {

    private final SubmissionCommandService submissionCommandService;

    public SubmissionCommandController(SubmissionCommandService submissionCommandService) {
        this.submissionCommandService = submissionCommandService;
    }

    @PostMapping
    public ApiResponse<SubmissionResponse> submitAssignment(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody SubmissionCreateRequest request) {
        return ApiResponse.success(submissionCommandService.submitAssignment(userId, userRole, request));
    }

    @PostMapping("/submit")
    public ApiResponse<SubmissionResponse> submitAssignmentLegacy(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody SubmissionCreateRequest request) {
        return ApiResponse.success(submissionCommandService.submitAssignment(userId, userRole, request));
    }

    @PostMapping("/grade")
    public ApiResponse<Void> gradeSubmission(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody SubmissionGradeRequest request) {
        submissionCommandService.gradeSubmission(userId, userRole, request);
        return ApiResponse.success();
    }
}
