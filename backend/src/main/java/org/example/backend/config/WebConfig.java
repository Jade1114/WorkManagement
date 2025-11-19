package org.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**"); // 所有 API 需要 token
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")       // 允许所有前端域名访问，之后可按需缩小
                .allowedMethods("*")       // GET POST PUT DELETE …
                .allowedHeaders("*")       // 允许所有请求头
                .exposedHeaders("Authorization") // 允许前端读取 Authorization 响应头
                .allowCredentials(false);  // 若需要携带 cookie 改为 true
    }
}