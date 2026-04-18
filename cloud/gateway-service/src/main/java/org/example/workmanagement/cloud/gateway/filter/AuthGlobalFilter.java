package org.example.workmanagement.cloud.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.example.workmanagement.cloud.gateway.common.ApiResponse;
import org.example.workmanagement.cloud.gateway.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthGlobalFilter.class);

    private static final List<String> WHITELIST = List.of(
            "/user/auth/login",
            "/user/internal/config"
    );

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthGlobalFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isWhitelisted(path)) {
            log.info("gateway auth bypassed: path={}", path);
            return chain.filter(exchange);
        }

        if (!path.startsWith("/education/")) {
            return chain.filter(exchange);
        }

        log.info("gateway auth required: path={}", path);
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("gateway auth failed: reason=missing bearer token, path={}", path);
            return writeUnauthorized(exchange, "未登录或 token 格式错误");
        }

        String token = authorization.substring(7).trim();
        try {
            DecodedJWT jwt = jwtUtils.verifyAndDecode(token);
            Long userId = jwtUtils.getUserId(jwt);
            String role = jwtUtils.getRole(jwt);

            log.info("gateway auth success: userId={}, role={}, path={}", userId, role, path);
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.warn("gateway auth failed: reason=invalid token, path={}", path);
            return writeUnauthorized(exchange, "未登录或 token 无效");
        }
    }

    private boolean isWhitelisted(String path) {
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> writeUnauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Void> body = ApiResponse.error(401, message);
        try {
            byte[] bytes = objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
