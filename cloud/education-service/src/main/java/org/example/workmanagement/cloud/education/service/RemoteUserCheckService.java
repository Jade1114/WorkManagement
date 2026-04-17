package org.example.workmanagement.cloud.education.service;

import org.example.workmanagement.cloud.education.client.UserServiceClient;
import org.example.workmanagement.cloud.education.dto.UserCheckResult;
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
}
