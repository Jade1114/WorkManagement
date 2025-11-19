package org.example.backend.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor interceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/**")          // 拦截所有 /api 开头的接口
                .excludePathPatterns(
                        "/api/auth/**",             // 放行登录、注册等
                        "/error"                    // Spring Boot 默认错误接口
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")                 // 允许所有前端域名
                .allowedMethods("*")                 // GET POST PUT DELETE 等
                .allowedHeaders("*")                 // 允许所有请求头
                .exposedHeaders("Authorization")     // 允许前端读取 token
                .allowCredentials(false)             // 不携带 cookie
                .maxAge(3600);                       // 预检请求缓存 1 小时
    }
}