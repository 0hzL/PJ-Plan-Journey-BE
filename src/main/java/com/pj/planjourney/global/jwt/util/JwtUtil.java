package com.pj.planjourney.global.jwt.util;

import com.pj.planjourney.domain.refreshtoken.Token;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.refreshtoken.repository.RefreshTokenRepository;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_TIME = 24 * 60 * 60 * 1000L; // 테스트를 위해 1분 단위로 수정해둠
    public static final long REFRESH_TOKEN_TIME = 24 * 60 * 60 * 1000L; // 24시간
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;


    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getUserInfoFromToken(token);
        String rolesString = claims.get(AUTHORIZATION_KEY, String.class);
        return Arrays.asList(rolesString.split(","));
    }


    // 토큰 생성
    public String createAccessToken(String email, Collection<? extends GrantedAuthority> authorities) {
        Date date = new Date();
        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim(AUTHORIZATION_KEY, roles)
                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // refresh 토큰 생성
    public String createRefreshToken(String email) {
        Date date = new Date();

        String token = Jwts.builder()
                .claim("email", email)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
        //저장 부터 service단에서
        Token refreshToken = new Token(token, email);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    // Header에서 Access Token 가져오기
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Header에서 Refresh Token 가져오기
    public String getRefreshTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("RefreshToken");
        if (StringUtils.hasText(token)) {
            return token;
        }
        return null;
    }

    // 토큰 검증
    public String validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return null; // 토큰이 유효한 경우
        } catch (SecurityException | MalformedJwtException e) {
            return "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.";
        } catch (ExpiredJwtException e) {
            return "Expired JWT token, 만료된 JWT token 입니다.";
        } catch (UnsupportedJwtException e) {
            return "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.";
        } catch (IllegalArgumentException e) {
            return "JWT claims is empty, 잘못된 JWT 토큰 입니다.";
        }
    }


    // 토큰에서 사용자 정보 및 권한 정보를 가져오기
    public UserDetailsImpl getUserDetailsFromToken(String token, User user) {
        Claims claims = getUserInfoFromToken(token);
        List<String> roles = List.of(claims.get(AUTHORIZATION_KEY).toString().split(","));
        return UserDetailsImpl.from(user, roles);
    }


    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

}