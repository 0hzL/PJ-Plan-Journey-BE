package com.pj.planjourney.domain.user.dto;

import com.pj.planjourney.domain.user.entity.User;
import lombok.Getter;

@Getter
public class LoginResponseDto {

    private String nickname;
    private String email;
    private String accessToken;
    private String refreshToken;

    public LoginResponseDto(String nickname, String email, String accessToken, String refreshToken) {
        this.nickname = nickname;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
