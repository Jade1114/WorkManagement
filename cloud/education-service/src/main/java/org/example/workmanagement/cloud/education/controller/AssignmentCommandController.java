package org.example.workmanagement.cloud.education.controller;

import org.example.workmanagement.cloud.education.dto.AssignmentCreateRequest;
import org.example.workmanagement.cloud.education.service.AssignmentCommandService;
import org.example.workmanagement.cloud.education.vo.AssignmentResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/assignments")
public class AssignmentCommandController {

    private final AssignmentCommandService assignmentCommandService;

    public AssignmentCommandController(AssignmentCommandService assignmentCommandService) {
        this.assignmentCommandService = assignmentCommandService;
    }

    @PostMapping("/create")
    public AssignmentResponse createAssignment(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody AssignmentCreateRequest request) {
        return assignmentCommandService.createAssignment(userId, userRole, request);
    }
}
