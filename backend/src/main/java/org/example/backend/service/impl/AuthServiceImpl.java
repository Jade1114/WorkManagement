package org.example.backend.service.impl;

import jakarta.annotation.Resource;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.dto.WechatLoginRequest;
import org.example.backend.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.AuthService;
import org.example.backend.util.JwtUtil;
import org.example.backend.vo.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {


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
        user.setPassword(encoder.encode(req.getPassword()));  // BCrypt 加密
        user.setRole("student");  

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw e; 
        }
    }

    @Override
    public LoginResponse login(LoginRequest req) {

        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 1. 查询用户是否存在
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 2. 校验密码
        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new RuntimeException("账号已禁用");
        }

        // 3. 生成 token
        String token = jwtUtil.createToken(user.getId(), user.getRole());

        // 4. 返回 LoginResponse
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public LoginResponse wechatLogin(WechatLoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getCode())) {
            throw new RuntimeException("code 不能为空");
        }
        if (!StringUtils.hasText(appid) || !StringUtils.hasText(secret)) {
            throw new RuntimeException("微信小程序 appid/secret 未配置");
        }

        // 1) 请求微信 jscode2session
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, request.getCode());
        ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = resp.getBody();
        if (body == null) {
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

        // 2) 绑定或创建用户（学生）
        User user = userRepository.findByOpenid(openid);
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setRole("student");
            user.setPassword(encoder.encode(openid)); // 占位密码
            user.setUsername(generateUsername(openid));
            userRepository.save(user);
        }

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new RuntimeException("账号已禁用");
        }

        // 3) 生成 token
        String token = jwtUtil.createToken(user.getId(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    private String generateUsername(String openid) {
        String base = "wx_" + openid.substring(0, Math.min(8, openid.length()));
        String candidate = base;
        int i = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + "_" + i;
            i++;
        }
        return candidate;
    }
}
