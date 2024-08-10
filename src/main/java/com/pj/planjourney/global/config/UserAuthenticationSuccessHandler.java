package com.pj.planjourney.global.config;

import com.pj.planjourney.domain.refreshtoken.service.RefreshTokenService;
import com.pj.planjourney.domain.user.service.UserService;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import com.pj.planjourney.global.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @Autowired
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
            String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getAuthorities());
            String refreshToken = jwtUtil.createRefreshToken(user.getUser().getEmail());
            String email = user.getUser().getEmail();

            // Save refresh token
            refreshTokenService.saveRefreshToken(email, refreshToken);
            log.info(refreshToken);
            response.setContentType("application/json");
            response.setHeader("Authorization", accessToken);
            response.setHeader("RefreshToken", refreshToken);
            //response.getWriter().write("{\"accessToken\":\"" + accessToken + "\", \"refreshToken\":\"" + refreshToken + "\"}");

            userService.cancelDeactivation(user.getUser().getId());
            log.info("로그인 성공 및 JWT 생성");
        } catch (Exception e) {
            log.error("로그인 성공 핸들러 오류", e);
            throw new IOException("로그인 성공 핸들러 오류", e);
        }
    }
}
