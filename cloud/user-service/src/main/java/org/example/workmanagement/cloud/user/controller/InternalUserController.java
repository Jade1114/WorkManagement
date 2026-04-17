package org.example.workmanagement.cloud.user.controller;

import org.example.workmanagement.cloud.user.service.InternalUserQueryService;
import org.example.workmanagement.cloud.user.vo.UserCheckResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final InternalUserQueryService internalUserQueryService;

    public InternalUserController(InternalUserQueryService internalUserQueryService) {
        this.internalUserQueryService = internalUserQueryService;
    }

    @GetMapping("/check")
    public UserCheckResponse checkPublisher(@RequestParam Long publisherId) {
        return internalUserQueryService.checkPublisher(publisherId);
    }
}
