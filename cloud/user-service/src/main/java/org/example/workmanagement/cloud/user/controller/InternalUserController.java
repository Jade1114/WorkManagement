package org.example.workmanagement.cloud.user.controller;

import java.util.List;

import org.example.workmanagement.cloud.user.service.InternalUserQueryService;
import org.example.workmanagement.cloud.user.vo.UserCheckResponse;
import org.example.workmanagement.cloud.user.vo.UserSummaryResponse;
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
    public UserCheckResponse checkPublisher(@RequestParam("publisherId") Long publisherId) {
        return internalUserQueryService.checkPublisher(publisherId);
    }

    @GetMapping("/summaries")
    public List<UserSummaryResponse> listSummaries(@RequestParam("ids") List<Long> ids) {
        return internalUserQueryService.listSummaries(ids);
    }

    @GetMapping("/count")
    public Long countByRole(@RequestParam("role") String role) {
        return internalUserQueryService.countByRole(role);
    }
}
