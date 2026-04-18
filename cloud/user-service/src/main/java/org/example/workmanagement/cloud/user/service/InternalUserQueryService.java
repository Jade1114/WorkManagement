package org.example.workmanagement.cloud.user.service;

import java.util.List;

import org.example.workmanagement.cloud.user.entity.User;
import org.example.workmanagement.cloud.user.mapper.UserMapper;
import org.example.workmanagement.cloud.user.vo.UserCheckResponse;
import org.example.workmanagement.cloud.user.vo.UserSummaryResponse;
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

    public List<UserSummaryResponse> listSummaries(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return userMapper.selectByIds(ids).stream()
                .map(user -> new UserSummaryResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole(),
                        user.getActive()))
                .toList();
    }

    public long countByRole(String role) {
        return userMapper.countByRole(role);
    }
}
