package org.example.workmanagement.cloud.education.service;

import java.util.List;

import org.example.workmanagement.cloud.education.client.UserServiceClient;
import org.example.workmanagement.cloud.education.dto.UserCheckResult;
import org.example.workmanagement.cloud.education.dto.UserSummaryResult;
import org.springframework.stereotype.Service;

@Service
public class RemoteUserCheckService {

    private final UserServiceClient userServiceClient;

    public RemoteUserCheckService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public UserCheckResult checkPublisher(Long publisherId) {
        return userServiceClient.checkPublisher(publisherId);
    }

    public UserCheckResult checkUser(Long userId) {
        return userServiceClient.checkPublisher(userId);
    }

    public List<UserSummaryResult> listUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return userServiceClient.listSummaries(ids);
    }

    public long countUsersByRole(String role) {
        Long result = userServiceClient.countByRole(role);
        return result == null ? 0 : result;
    }
}
