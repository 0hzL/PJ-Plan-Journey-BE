package com.pj.planjourney.global.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseMessage {
    USER_CREATED(HttpStatus.CREATED, "회원가입에 성공했습니다."),
    USER_RETRIEVED(HttpStatus.OK, "유저 조회에 성공했습니다."),
    USERS_RETRIEVED(HttpStatus.OK, "유저 목록 조회에 성공했습니다."),
    USER_DELETED(HttpStatus.OK, "회원 삭제에 성공했습니다."),
    PLAN_DELETED(HttpStatus.OK, "일정 생성에 성공했습니다."),
    SUCCESS(HttpStatus.OK,"성공했습니다.");


    private final HttpStatus httpStatus;
    private final String message;

    ApiResponseMessage(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

