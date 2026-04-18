package org.example.workmanagement.cloud.education.controller;

import java.util.List;

import org.example.workmanagement.cloud.education.common.ApiResponse;
import org.example.workmanagement.cloud.education.service.AssignmentQueryService;
import org.example.workmanagement.cloud.education.vo.AssignmentListItemResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assignments")
public class AssignmentQueryController {

    private final AssignmentQueryService assignmentQueryService;

    public AssignmentQueryController(AssignmentQueryService assignmentQueryService) {
        this.assignmentQueryService = assignmentQueryService;
    }

    @GetMapping
    public ApiResponse<List<AssignmentListItemResponse>> listAssignments(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(assignmentQueryService.listAssignments(userId, userRole));
    }
}
