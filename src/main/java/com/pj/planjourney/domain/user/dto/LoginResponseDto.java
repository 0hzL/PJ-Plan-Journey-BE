package com.pj.planjourney.domain.user.dto;

import com.pj.planjourney.domain.user.entity.User;
import lombok.Getter;

@Getter
public class LoginResponseDto {

    private String nickname;
    private String email;

    public LoginResponseDto(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
