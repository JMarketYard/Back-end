package com.example.demo.base.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.example.demo.base.code.BaseCode;
import com.example.demo.base.code.ReasonDTO;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 0. Yoon - 공통 성공
    _OK(HttpStatus.OK, "COMMON_200", "성공입니다."),

    // 1. Yoon - 회원 관련 성공
    USER_SIGNUP_SUCCESS(HttpStatus.OK, "USER_2001", "회원가입이 성공적으로 완료되었습니다."),
    USER_WITHDRAWN_SUCCESS(HttpStatus.OK, "USER_2002", "성공적으로 탈퇴하였습니다. 일정 시간 내로 재로그인 하지 않을시, 모든 정보가 삭제됩니다."),
    USER_LOGIN_SUCCESS(HttpStatus.OK, "USER_2003", "로그인에 성공하였습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2004", "성공적으로 로그아웃하였습니다."),

    // 2. Yoon - 토큰 관련 성공
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "TOKEN_2001", "리프레시 토큰을 통한 토큰 갱신에 성공하였습니다."),

    // 3. Yoon - 관리자 모드 관련 성공
    ADMIN_TURN_ADMIN(HttpStatus.OK, "ADMIN_2001", "관리자 모드로 변환에 성공하였습니다."),
    ADMIN_TURN_USER(HttpStatus.OK, "ADMIN_2002", "사용자 모드로 변환에 성공하였습니다."),
    ADMIN_GET_ALL_USER(HttpStatus.OK, "ADMIN_2003", "모든 사용자를 조회하였습니다."),

    // 4. HyungJin - 래플 관련 성공
    RAFFLE_UPLOAD_SUCCESS(HttpStatus.OK, "RAFFLE_2001", "래플 업로드에 성공하였습니다."),
    RAFFLE_FETCH_SUCCESS(HttpStatus.OK, "RAFFLE_2002", "래플 조회에 성공하였습니다."),


    // 5. HyungJin - 사진 관련 성공
    IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "IMAGE_2001", "사진 업로드에 성공하였습니다."),

    // 6. Huiju - 리다이렉트 관련 성공
    REDILECT_SUCCESS(HttpStatus.FOUND, "REDILECT_3021", "개최자를 리다이렉트하는 데에 성공하였습니다."),

    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return getReason();
    }
}