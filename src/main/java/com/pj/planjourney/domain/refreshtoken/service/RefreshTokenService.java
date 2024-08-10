package com.pj.planjourney.domain.refreshtoken.service;

import com.pj.planjourney.domain.refreshtoken.Token;
import com.pj.planjourney.domain.refreshtoken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String email, String refreshToken) {
        try {
            // 기존의 리프레시 토큰이 있는 경우 블랙리스트에 추가
            String existingRefreshToken = redisTemplate.opsForValue().get("refreshToken:" + email);
            if (existingRefreshToken != null) {
                invalidateToken(existingRefreshToken);  // 블랙리스트에 추가
            }
            // 새로운 리프레시 토큰 저장
            redisTemplate.opsForValue().set("refreshToken:" + email, refreshToken);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to save refresh token: " + e.getMessage());
        }
    }

    public String getRefreshToken(Long userId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get("refreshToken:" + userId);
    }

    public void invalidateToken(String token) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = "blacklist:" + token;
        valueOperations.set(key, token, 24, TimeUnit.HOURS);

    }

    public boolean isTokenBlacklisted(String token) {
        ValueOperations<String, String > valueOperations = redisTemplate.opsForValue();
        return valueOperations.get("blacklist:" + token) != null;
    }

    public void deleteRefreshToken(Long userId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.getOperations().delete("refreshToken:" + userId);
    }
}
