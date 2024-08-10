package com.pj.planjourney.global.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pj.planjourney.domain.refreshtoken.service.RefreshTokenService;
import com.pj.planjourney.global.auth.service.UserDetailsServiceImpl;
import com.pj.planjourney.global.common.response.ApiResponse;
import com.pj.planjourney.global.common.response.ApiResponseMessage;
import com.pj.planjourney.global.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isLogoutRequest(req)) {
                handleLogout(req, res);
                return; // 로그아웃 처리 후 필터 체인의 나머지 부분을 실행하지 않도록 합니다.
            }

            String accessToken = jwtUtil.getAccessTokenFromHeader(req);
            String refreshToken = jwtUtil.getRefreshTokenFromHeader(req);

            if (StringUtils.hasText(accessToken)) {
                processAccessToken(accessToken, refreshToken, req, res, filterChain);
            } else if (StringUtils.hasText(refreshToken)) {
                validateRefreshToken(refreshToken, req, res, filterChain);
            } else {
                filterChain.doFilter(req, res); // 토큰이 없는 경우 필터 체인의 다음 필터로 요청을 전달합니다.
            }

        } catch (Exception e) {
            log.error("Exception occurred during filter processing", e);
            handleExceptionInFilter(e, res); // 예외 발생 시 적절한 처리
        }
    }


    private void processAccessToken(String accessToken, String refreshToken, HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        String tokenValidationResult = jwtUtil.validateToken(accessToken);

        if (tokenValidationResult != null) {
            handleExpiredAccessToken(refreshToken, req, res, filterChain);
        } else {
            setAuthentication(jwtUtil.getUserInfoFromToken(accessToken).getSubject());
            filterChain.doFilter(req, res);
        }
    }

    private void handleExpiredAccessToken(String refreshToken, HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        if (StringUtils.hasText(refreshToken)) {
            validateRefreshToken(refreshToken, req, res, filterChain);
        } else {
            log.error("Refresh Token is missing");
            throw new AuthenticationCredentialsNotFoundException("Refresh Token is missing");
        }
    }

    private void validateRefreshToken(String refreshToken, HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        String refreshTokenValidation = jwtUtil.validateToken(refreshToken);

        if (refreshTokenValidation == null) {
            Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
            log.info("Claims: " + claims.toString());

            Object emailObj = claims.get("email");
            String email = emailObj != null ? emailObj.toString() : null;

            log.info("Validating refresh token with email: " + email);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (userDetails == null) {
                log.error("User details not found for email: " + email);
                throw new UsernameNotFoundException("User not found for email: " + email);
            }

            String newAccessToken = jwtUtil.createAccessToken(email, userDetails.getAuthorities());
            res.setHeader("Authorization", newAccessToken);

            setAuthentication(email);
        } else {
            log.error("Invalid refresh token: " + refreshTokenValidation);
            throw new AuthenticationCredentialsNotFoundException("Invalid refresh token");
        }

        filterChain.doFilter(req, res);
    }


    private void handleExceptionInFilter(Exception e, HttpServletResponse res) throws IOException {
        if (!res.isCommitted()) { // 응답이 이미 커밋된 상태가 아니면
            ApiResponse<Void> apiResponse = new ApiResponse<>(null, ApiResponseMessage.ERROR);
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            try (PrintWriter writer = res.getWriter()) {
                writer.write(new ObjectMapper().writeValueAsString(apiResponse));
            }
        } else {
            log.error("Response already committed, unable to handle exception: {}", e.getMessage());
        }
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) && "/users/logout".equalsIgnoreCase(request.getRequestURI());
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        String refreshToken = jwtUtil.getRefreshTokenFromHeader(request);
        try {
            if (accessToken != null) {
                refreshTokenService.invalidateToken(accessToken);
            }
            if (refreshToken != null) {
                refreshTokenService.invalidateToken(refreshToken);
                Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
                if (claims != null) {
                    String userIdString = claims.getSubject();
                    if (userIdString != null) {
                        Long userId = Long.valueOf(userIdString);
                        refreshTokenService.deleteRefreshToken(userId);
                        log.info("Deleted refresh token for user ID: " + userId);
                    }
                }
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("Logout handling failed: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
