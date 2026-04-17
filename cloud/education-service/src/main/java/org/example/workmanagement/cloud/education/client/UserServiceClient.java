package org.example.workmanagement.cloud.education.client;

import org.example.workmanagement.cloud.education.dto.UserCheckResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/internal/users/check")
    UserCheckResult checkPublisher(@RequestParam Long publisherId);
}
