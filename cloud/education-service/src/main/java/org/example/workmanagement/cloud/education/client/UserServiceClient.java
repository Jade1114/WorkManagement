package org.example.workmanagement.cloud.education.client;

import java.util.List;

import org.example.workmanagement.cloud.education.dto.UserCheckResult;
import org.example.workmanagement.cloud.education.dto.UserSummaryResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/internal/users/check")
    UserCheckResult checkPublisher(@RequestParam("publisherId") Long publisherId);

    @GetMapping("/internal/users/summaries")
    List<UserSummaryResult> listSummaries(@RequestParam("ids") List<Long> ids);

    @GetMapping("/internal/users/count")
    Long countByRole(@RequestParam("role") String role);
}
