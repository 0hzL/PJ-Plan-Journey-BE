package com.pj.planjourney.domain.refreshtoken.controller;

import com.pj.planjourney.domain.refreshtoken.service.RefreshTokenService;
import com.pj.planjourney.global.auth.service.UserDetailsServiceImpl;
import com.pj.planjourney.global.common.response.ApiResponse;
import com.pj.planjourney.global.common.response.ApiResponseMessage;
import com.pj.planjourney.global.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestHeader("Authorization") String accessToken,
                                                            @RequestHeader("RefreshToken") String refreshToken) {
        try {
            String refreshTokenValidation = jwtUtil.validateToken(refreshToken);

            if (refreshTokenValidation != null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(null, ApiResponseMessage.INVALID_REFRESH_TOKEN));
            }

            Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
            Object emailObj = claims.get("email");
            String email = emailObj != null ? emailObj.toString() : null;
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String newAccessToken = jwtUtil.createAccessToken(email, userDetails.getAuthorities());

            return ResponseEntity.ok(new ApiResponse<>(newAccessToken, ApiResponseMessage.TOKEN_REFRESHED));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, ApiResponseMessage.ERROR));
        }
    }
}