package org.example.workmanagement.cloud.user.service;

import java.util.List;

import org.example.workmanagement.cloud.user.common.BusinessException;
import org.example.workmanagement.cloud.user.dto.AdminUpdateUserRequest;
import org.example.workmanagement.cloud.user.dto.ChangePasswordRequest;
import org.example.workmanagement.cloud.user.entity.User;
import org.example.workmanagement.cloud.user.mapper.UserMapper;
import org.example.workmanagement.cloud.user.vo.AdminUserResponse;
import org.example.workmanagement.cloud.user.vo.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {

    private static final Logger log = LoggerFactory.getLogger(UserManagementService.class);

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserManagementService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserResponse getCurrentUser(Long userId) {
        log.info("get current user start: userId={}", userId);

        User user = requireUser(userId);
        log.info("get current user success: userId={}, role={}", user.getId(), user.getRole());
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }

    public String changePassword(Long userId, ChangePasswordRequest request) {
        log.info("change password start: userId={}", userId);

        User user = requireUser(userId);
        if (!encoder.matches(request.oldPassword(), user.getPassword())) {
            log.warn("change password failed: userId={}, reason=old password mismatch", userId);
            throw new BusinessException("旧密码错误");
        }

        userMapper.updatePassword(userId, encoder.encode(request.newPassword()));
        log.info("change password success: userId={}", userId);
        return "密码修改成功";
    }

    public List<UserResponse> listStudents(Long userId, String userRole) {
        log.info("list students start: userId={}, role={}", userId, userRole);
        requireTeacherOrAdmin(userId, userRole, "当前用户无权限查看学生列表");

        List<UserResponse> result = userMapper.selectByRoleOrderById("student").stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getRole()))
                .toList();

        log.info("list students success: userId={}, count={}", userId, result.size());
        return result;
    }

    public List<AdminUserResponse> listUsersForAdmin(Long userId, String userRole) {
        log.info("list admin users start: userId={}, role={}", userId, userRole);
        requireAdmin(userId, userRole, "当前用户无权限查看用户列表");

        List<AdminUserResponse> result = userMapper.selectAllOrderById().stream()
                .map(user -> new AdminUserResponse(user.getId(), user.getUsername(), user.getRole(), user.getActive()))
                .toList();

        log.info("list admin users success: userId={}, count={}", userId, result.size());
        return result;
    }

    public void updateUser(Long currentUserId, String currentUserRole, Long targetUserId, AdminUpdateUserRequest request) {
        log.info("admin update user start: currentUserId={}, targetUserId={}",
                currentUserId, targetUserId);
        requireAdmin(currentUserId, currentUserRole, "当前用户无权限更新用户");

        User user = requireUser(targetUserId);
        String role = normalizeRole(request.role());
        if (role != null && !List.of("admin", "teacher", "student").contains(role)) {
            log.warn("admin update user failed: targetUserId={}, role={}, reason=invalid role",
                    targetUserId, role);
            throw new BusinessException("用户角色不合法");
        }
        if (role == null && request.active() == null) {
            log.info("admin update user skipped: targetUserId={}, reason=no changed fields", targetUserId);
            return;
        }

        userMapper.updateRoleAndActive(user.getId(), role, request.active());
        log.info("admin update user success: currentUserId={}, targetUserId={}",
                currentUserId, user.getId());
    }

    private User requireUser(Long userId) {
        if (userId == null) {
            log.warn("user operation failed: reason=userId missing");
            throw new BusinessException("当前用户不存在");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("user operation failed: userId={}, reason=user not found", userId);
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private void requireTeacherOrAdmin(Long userId, String userRole, String message) {
        if (userId == null) {
            log.warn("user permission check failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (userRole == null || userRole.isBlank()) {
            log.warn("user permission check failed: userId={}, reason=role missing", userId);
            throw new BusinessException("当前用户角色缺失");
        }
        if (!"teacher".equals(userRole) && !"admin".equals(userRole)) {
            log.warn("user permission check failed: userId={}, role={}, reason=role not allowed",
                    userId, userRole);
            throw new BusinessException(message);
        }
    }

    private void requireAdmin(Long userId, String userRole, String message) {
        if (userId == null) {
            log.warn("admin permission check failed: reason=current user missing");
            throw new BusinessException("当前用户不存在");
        }
        if (!"admin".equals(userRole)) {
            log.warn("admin permission check failed: userId={}, role={}, reason=role not allowed",
                    userId, userRole);
            throw new BusinessException(message);
        }
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        return role.trim();
    }
}
