package org.example.workmanagement.cloud.user.service;

import org.example.workmanagement.cloud.user.entity.User;
import org.example.workmanagement.cloud.user.mapper.UserMapper;
import org.example.workmanagement.cloud.user.vo.UserCheckResponse;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class InternalUserQueryService {

    @Resource
    UserMapper userMapper;

    public UserCheckResponse checkPublisher(Long publisherId) {
        User result = userMapper.selectById(publisherId);
        if (result == null) {
            return new UserCheckResponse(false, null, false);
        }
        return new UserCheckResponse(true, result.getRole(), result.getActive());
    }
}
