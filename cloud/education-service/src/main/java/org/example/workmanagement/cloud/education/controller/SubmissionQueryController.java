package org.example.workmanagement.cloud.education.controller;

import java.util.List;

import org.example.workmanagement.cloud.education.common.ApiResponse;
import org.example.workmanagement.cloud.education.service.SubmissionQueryService;
import org.example.workmanagement.cloud.education.vo.SubmissionListItemResponse;
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
}
