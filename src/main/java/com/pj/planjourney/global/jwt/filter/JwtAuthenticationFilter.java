package com.pj.planjourney.global.jwt.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pj.planjourney.domain.refreshtoken.service.RefreshTokenService;
import com.pj.planjourney.domain.user.dto.LoginRequestDto;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import com.pj.planjourney.global.common.response.ApiResponse;
import com.pj.planjourney.global.common.response.ApiResponseMessage;
import com.pj.planjourney.global.jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Component
@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        super.setAuthenticationManager(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            LoginRequestDto requestDto = new ObjectMapper().readValue(body, LoginRequestDto.class);

            UsernamePasswordAuthenticationToken authRequest
                    = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
            setDetails(request, authRequest);
            return getAuthenticationManager().authenticate( // authenticate인증처리 매서드
                    authRequest
            );
        } catch (IOException e) {
            log.info("Error reading request body: " + e.getMessage());
            throw new RuntimeException("Failed to read request body", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request data: " + e.getMessage());
            throw new RuntimeException("Invalid request data", e);
        }
    }





    /*
    * 로그인 성공 시 -> token 생성 및 헤더로 token 보내줍니다.
    * 기존의 refresh token 이 존재 및 유효할 경우 : 기존의 refresh token 그대로 사용
    * 그 외 : refresh token 재발급 및 기존의 refresh token 은 blackList 에 추가(무효화)
    * */
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        log.info("로그인 성공 및 JWT 생성");
        // Call success handler
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String email = userDetails.getUsername();
        Long id = userDetails.getUser().getId();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        String accessToken = jwtUtil.createAccessToken(email, authorities);
        String refreshToken;

        String previousRefreshToken = refreshTokenService.getRefreshToken(id);
        if (previousRefreshToken != null&& !refreshTokenService.isTokenBlacklisted(previousRefreshToken)) {
           refreshToken = previousRefreshToken;
        } else{
            refreshToken = jwtUtil.createRefreshToken(email);

            if(previousRefreshToken != null){
                refreshTokenService.invalidateToken(previousRefreshToken);
            }
            refreshTokenService.saveRefreshToken(email, refreshToken);
        }

        response.setHeader("Authorization", accessToken);
        response.setHeader("RefreshToken", refreshToken);
    }

}