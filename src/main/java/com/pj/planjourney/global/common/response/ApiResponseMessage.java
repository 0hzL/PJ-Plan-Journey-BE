package com.pj.planjourney.global.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseMessage {
    USER_CREATED(HttpStatus.CREATED, "회원가입에 성공했습니다."),
    USER_LOGIN(HttpStatus.OK,"로그인에 성공했습니다."),
    USER_LOGOUT(HttpStatus.OK,"로그아웃에 성공하였습니다."),
    USER_CHANGED(HttpStatus.OK,"회원 정보가 수정되었습니다."),
    USER_RETRIEVED(HttpStatus.OK, "유저 조회에 성공했습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN , "유효하지 않은 refresh token 입니다"),
    VALID_REFRESH_TOKEN(HttpStatus.OK , "유효한 refresh token 입니다"),
    TOKEN_REFRESHED(HttpStatus.OK,"새로운 access token을 생성했습니다"),
    USERS_RETRIEVED(HttpStatus.OK, "유저 목록 조회에 성공했습니다."),
    USER_DELETED(HttpStatus.OK, "회원 삭제에 성공했습니다."),
    REQUEST_SENT(HttpStatus.OK, "친구 요청이 전송되었습니다."),
    REQUEST_ACCEPTED(HttpStatus.OK, "친구 요청이 수락되었습니다."),
    REQUEST_REJECTED(HttpStatus.OK, "친구 요청이 거절되었습니다."),
    REQUEST_RETRIEVED(HttpStatus.OK, "친구 요청목록이 조회되었습니다."),
    RECEIVED_RETRIEVED(HttpStatus.OK, "받은 친구 요청목록이 조회되었습니다."),
    FRIENDS_RETRIEVED(HttpStatus.OK, "친구 목록이 조회되었습니다."),
    FRIEND_DELETED(HttpStatus.OK, "친구가 삭제되었습니다."),
    PLAN_CREATED(HttpStatus.OK, "일정 생성에 성공했습니다."),
    ERROR(HttpStatus.BAD_REQUEST,"잘못된 요청입니다."),
    SUCCESS(HttpStatus.OK,"성공했습니다."),
    PLAN_UPDATE_TITLE(HttpStatus.OK, "제목이 수정되었습니다."),
    PLAN_DELETE(HttpStatus.OK, "일정이 삭제되었습니다."),
    PLAN_PUBLISH(HttpStatus.OK, "일정이 공개되었습니다."),
    PLAN_COPY(HttpStatus.OK, "일정을 가져왔습니다."),
    CHILD_COMMENT_CREATE(HttpStatus.CREATED, "대댓글 작성에 성공했습니다."),
    CHILD_COMMENT_UPDATE(HttpStatus.OK, "대댓글을 수정했습니다."),
    CHILD_COMMENT_DELETE(HttpStatus.OK, "대댓글을 삭제했습니다."),
    COMMENT_CREATE(HttpStatus.CREATED, "댓글을 작성했습니다."),
    COMMENT_UPDATE(HttpStatus.OK, "댓글을 수정했습니다."),
    COMMENT_DELETE(HttpStatus.OK, "댓글을 삭제했습니다.");



    private final HttpStatus httpStatus;
    private final String message;

    ApiResponseMessage(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

