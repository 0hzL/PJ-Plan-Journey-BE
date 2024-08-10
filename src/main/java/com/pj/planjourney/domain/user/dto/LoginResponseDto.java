package com.pj.planjourney.domain.user.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;

    public LoginResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
