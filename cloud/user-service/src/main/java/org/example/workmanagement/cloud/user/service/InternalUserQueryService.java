package org.example.workmanagement.cloud.user.service;

import org.example.workmanagement.cloud.user.vo.UserCheckResponse;
import org.springframework.stereotype.Service;

@Service
public class InternalUserQueryService {

    public UserCheckResponse checkPublisher(Long publisherId) {
        return new UserCheckResponse(false, null, false);
    }
}
