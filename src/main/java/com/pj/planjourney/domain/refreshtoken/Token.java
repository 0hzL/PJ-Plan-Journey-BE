package com.pj.planjourney.domain.refreshtoken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "token", timeToLive = 10)
@Getter
@ToString
public class Token {
    @Id
    private String email;
    private String refreshToken;

    public Token(String token, String email) {
        this.refreshToken = token;
        this.email = email;
    }
}
