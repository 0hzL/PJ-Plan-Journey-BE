package com.pj.planjourney.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequestDto {

    private String nickname;
    private String password;

}
