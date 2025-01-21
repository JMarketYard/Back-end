package com.example.demo.base.status;

import com.example.demo.base.code.BaseErrorCode;
import com.example.demo.base.code.ErrorReasonDTO;
import com.example.demo.entity.Raffle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 0. Yoon - 공통 에러
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러가 발생했습니다. 관리자에게 문의하세요."),
    COMMON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_4001", "잘못된 요청입니다."),
    COMMON_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_4002", "인증이 필요합니다."),

    // 1. Yoon - 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER_4001", "해당 유저를 찾을 수 없습니다."),
    USER_ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "USER_4002", "이미 탈퇴한 유저입니다."),
    USER_ALREADY_LOGOUT(HttpStatus.BAD_REQUEST, "USER_4003", "이미 로그아웃한 유저입니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_4004", "이미 존재하는 사용자입니다."),
    USER_INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "USER_4005", "로그인 경로가 규칙에 맞지 않습니다."),
    USER_INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER_4006", "비밀번호 설정 규칙에 맞지 않습니다."),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_4007", "비밀번호가 잘못되었습니다."),

    // 2. Yoon - 토큰 관련 에러
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "TOKEN_4001", "토큰이 누락되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_4002", "해당 토큰을 찾을 수 없습니다."),
    TOKEN_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_4003", "만료되거나 잘못된 엑세스 토큰입니다."),
    TOKEN_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_4004", "만료되거나 잘못된 리프레시 토큰입니다."),

    // 3. Yoon - 관리자 모드 관련 에러
    ADMIN_UNAUTHORIZED_ACCESS(HttpStatus.BAD_REQUEST, "ADMIN_4001", "관리자만 접근 가능한 경로입니다."),

    // 4. Yoon - 소셜 로그인 관련 에러
    OAUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "OAUTH_4001", "OAuth 로그인에 실패했습니다."),
    OAUTH_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH_4002", "OAuth 로그인 처리에 실패했습니다."),

    // 5. Hyungjin - 래플 관련 에러
    RAFFLE_NOT_FOUND(HttpStatus.BAD_REQUEST, "RAFFLE_4001", "해당 래플을 찾을 수 없습니다."),

    // 6. Hyungjin - 카테고리 관련 에러
    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "CATEGORY_4001", "해당 카테고리를 찾을 수 없습니다."),

    // 7. Dohyun - 찜 관련 에러
    LIKE_ALREADY_FOUND(HttpStatus.BAD_REQUEST, "LIKE_4001", "이미 존재하는 찜입니다."),
    LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "LIKE_4002", "해당 찜을 찾을 수 없습니다."),

    // 8.Dohyun- 후기 관련 에러
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "REVIEW_4001", "해당 리뷰를 찾을 수 없습니다."),

    // 9. Huiju - 응모 관련 에러
    APPLY_INSUFFICIENT_TICKET(HttpStatus.BAD_REQUEST, "APPLY_4001", "보유한 티켓 수가 부족합니다."),
    APPLY_UNOPENED_RAFFLE(HttpStatus.BAD_REQUEST, "APPLY_4002", "아직 응모가 시작되지 않은 래플입니다."),
    APPLY_FINISHED_RAFFLE(HttpStatus.BAD_REQUEST, "APPLY_4003", "이미 종료된 래플입니다."),

    // 10. Huiju - Job 관련 에러
    JOB_EXECUTION_FAILED(HttpStatus.BAD_REQUEST, "JOB_4001", "Job 실행에 실패했습니다."),
    JOB_STORE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JOB_5001", "Job 저장에 실패했습니다."),
    JOB_UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "JOB_5002", "알 수 없는 Job 에러가 발생했습니다."),
    JOB_INTERRUPT(HttpStatus.SERVICE_UNAVAILABLE, "JOB_5031", "Job 인터럽트가 발생했습니다."),

    ;



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return getReason();
    }

}