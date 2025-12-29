package org.example.backend.service.impl;

import jakarta.annotation.Resource;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.dto.WechatBindRequest;
import org.example.backend.dto.WechatLoginRequest;
import org.example.backend.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.AuthService;
import org.example.backend.util.JwtUtil;
import org.example.backend.vo.LoginResponse;
import org.example.backend.vo.WechatLoginResult;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    private static final int BIND_TICKET_EXPIRE_SECONDS = 300;

    private static final ConcurrentHashMap<String, BindTicket> BIND_TICKET_STORE = new ConcurrentHashMap<>();

    @Resource
    private UserRepository userRepository;

    @Resource
    private JwtUtil jwtUtil;

    @Value("${wechat.appid:}")
    private String appid;

    @Value("${wechat.secret:}")
    private String secret;

    private final RestTemplate restTemplate = new RestTemplate();

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void register(RegisterRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new RuntimeException("密码不能为空");
        }

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole("student");

        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new RuntimeException("用户名或密码错误");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new RuntimeException("账号已禁用");
        }

        String token = jwtUtil.createToken(user.getId(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public WechatLoginResult wechatLogin(WechatLoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getCode())) {
            throw new RuntimeException("code 不能为空");
        }
        if (!StringUtils.hasText(appid) || !StringUtils.hasText(secret)) {
            throw new RuntimeException("微信小程序 appid/secret 未配置");
        }

        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, request.getCode());
        ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
        Map<String, Object> body;
        try {
            body = objectMapper.readValue(resp.getBody(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("微信返回无法解析: " + resp.getBody());
        }
        if (body == null || body.isEmpty()) {
            throw new RuntimeException("微信返回为空");
        }
        Object errcode = body.get("errcode");
        if (errcode != null && Integer.parseInt(String.valueOf(errcode)) != 0) {
            throw new RuntimeException("微信登录失败: " + body.getOrDefault("errmsg", "未知错误"));
        }
        String openid = (String) body.get("openid");
        if (!StringUtils.hasText(openid)) {
            throw new RuntimeException("微信登录失败: 未获取到 openid");
        }

        User user = userRepository.findByOpenid(openid);
        if (user != null) {
            if (Boolean.FALSE.equals(user.getActive())) {
                throw new RuntimeException("账号已禁用");
            }
            String token = jwtUtil.createToken(user.getId(), user.getRole());
            return new WechatLoginResult(false, null, null, token, user.getId(), user.getUsername(), user.getRole());
        }

        // 未绑定，返回 needBind + bindTicket
        String ticket = generateBindTicket(openid);
        return new WechatLoginResult(true, ticket, BIND_TICKET_EXPIRE_SECONDS, null, null, null, null);
    }

    @Override
    public LoginResponse wechatBind(WechatBindRequest request) {
        if (request == null || !StringUtils.hasText(request.getBindTicket())) {
            throw new RuntimeException("bindTicket 不能为空");
        }
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new RuntimeException("账号或密码不能为空");
        }

        BindTicket ticket = BIND_TICKET_STORE.remove(request.getBindTicket());
        if (ticket == null || ticket.isExpired()) {
            throw new RuntimeException("ticket 无效或已过期");
        }
        String openid = ticket.getOpenid();

        if (userRepository.existsByOpenid(openid)) {
            throw new RuntimeException("该微信已绑定其他账号");
        }

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null || !encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }
        if (Boolean.FALSE.equals(user.getActive())) {
            throw new RuntimeException("账号已禁用");
        }

        user.setOpenid(openid);
        userRepository.save(user);

        String token = jwtUtil.createToken(user.getId(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    private String generateBindTicket(String openid) {
        String ticket = UUID.randomUUID().toString();
        long expireAt = System.currentTimeMillis() + BIND_TICKET_EXPIRE_SECONDS * 1000L;
        BIND_TICKET_STORE.put(ticket, new BindTicket(openid, expireAt));
        return ticket;
    }

    private static class BindTicket {
        private final String openid;
        private final long expireAt;

        BindTicket(String openid, long expireAt) {
            this.openid = openid;
            this.expireAt = expireAt;
        }

        String getOpenid() {
            return openid;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }
    }
}
