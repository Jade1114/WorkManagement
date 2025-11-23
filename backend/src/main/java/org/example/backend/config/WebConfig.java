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
                .addPathPatterns("/api/**")          
                .excludePathPatterns(
                        "/api/auth/**",             
                        "/error"                    
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")                
                .allowedMethods("*")                 
                .allowedHeaders("*")                 
                .exposedHeaders("Authorization")     
                .allowCredentials(false)             
                .maxAge(3600);                       
    }
}