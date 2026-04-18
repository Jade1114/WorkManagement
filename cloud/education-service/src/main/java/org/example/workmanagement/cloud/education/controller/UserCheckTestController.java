package org.example.workmanagement.cloud.education.controller;

import org.example.workmanagement.cloud.education.common.ApiResponse;
import org.example.workmanagement.cloud.education.dto.UserCheckResult;
import org.example.workmanagement.cloud.education.service.RemoteUserCheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign-test")
public class UserCheckTestController {

    private final RemoteUserCheckService remoteUserCheckService;

    public UserCheckTestController(RemoteUserCheckService remoteUserCheckService) {
        this.remoteUserCheckService = remoteUserCheckService;
    }

    @GetMapping("/publisher")
    public ApiResponse<UserCheckResult> checkPublisher(@RequestParam Long publisherId) {
        return ApiResponse.success(remoteUserCheckService.checkPublisher(publisherId));
    }
}
