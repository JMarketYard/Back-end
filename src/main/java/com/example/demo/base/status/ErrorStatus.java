package com.example.demo.base.status;

import com.example.demo.base.code.BaseErrorCode;
import com.example.demo.base.code.ErrorReasonDTO;
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
    RAFFLE_USER_MISMATCH(HttpStatus.BAD_REQUEST, "REVIEW_4002","주최자와 래플이 일치하지 않습니다." ),
    NOT_WINNER(HttpStatus.BAD_REQUEST, "REVIEW_4003","당첨자가 아닙니다." ),
    DUPLICATE_REVIEW(HttpStatus.BAD_REQUEST, "REVIEW_4004","이미 리뷰를 작성하였습니다."),

    // 9. Dohyun - 문의 관련 에러
    INQUIRY_NOT_FOUND(HttpStatus.BAD_REQUEST, "INQUIRY_4001", "해당 문의를 찾을 수 없습니다."),

    // 10. Huiju - 응모 관련 에러
    APPLY_INSUFFICIENT_TICKET(HttpStatus.BAD_REQUEST, "APPLY_4001", "보유한 티켓 수가 부족합니다."),
    APPLY_UNOPENED_RAFFLE(HttpStatus.BAD_REQUEST, "APPLY_4002", "아직 응모가 시작되지 않은 래플입니다."),

    APPLY_FINISHED_RAFFLE(HttpStatus.BAD_REQUEST, "APPLY_4003", "이미 종료된 래플입니다."),
    APPLY_SELF_RAFFLE(HttpStatus.BAD_REQUEST, "APPLY_4004", "본인이 개최한 래플에는 응모할 수 없습니다."),
    APPLY_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "APPLY_4005", "이미 응모한 래플입니다."),

    // 11. Hyungjin - 이미지 관련 에러
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_4001", "이미지 업로드에 실패했습니다."),

    // 12. Huiju - Job 관련 에러
    JOB_EXECUTION_FAILED(HttpStatus.BAD_REQUEST, "JOB_4001", "Job 실행에 실패했습니다."),
    JOB_STORE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JOB_5001", "Job 저장에 실패했습니다."),
    JOB_UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "JOB_5002", "알 수 없는 Job 에러가 발생했습니다."),
    JOB_INTERRUPT(HttpStatus.SERVICE_UNAVAILABLE, "JOB_5031", "Job 인터럽트가 발생했습니다."),

    // 13. Huiju - 당첨자 추첨 관련 에러
    DRAW_EMPTY(HttpStatus.BAD_REQUEST, "DRAW_4001", "응모한 사용자가 없습니다."),
    DRAW_NO_WINNER_EMAIL(HttpStatus.BAD_REQUEST, "DRAW_4002", "당첨자의 이메일이 존재하지 않습니다."),
    DRAW_FAIL(HttpStatus.BAD_REQUEST, "DRAW_4003", "당첨자가 아닙니다."),
    DRAW_EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DRAW_5001", "이메일 전송에 실패하였습니다."),

    // 14. Huiju - 배송 관련 에러
    DELIVERY_NO_ADDRESS(HttpStatus.BAD_REQUEST, "DELIVERY_4001", "등록된 주소가 없습니다."),

    // 15. Huiju - 주소 관련 에러
    ADDRESS_MISMATCH_USER(HttpStatus.FORBIDDEN, "ADDRESS_4001", "선택한 주소가 해당 사용자에게 유효한 주소가 아닙니다."),

    // 16. Yoon - 결제 관련 에러
    PAYMENT_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "PAY_4001", "PAY API 요청 실패"),
    PAYMENT_REQUEST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAY_4002", "PAY API 요청 중 오류 발생"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_4041", "결제 정보가 없습니다"),
    PAYMENT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_4003", "결제 정보 저장 실패"),
    PAYMENT_HISTORY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_4004", "결제 내역 조회 중 오류 발생");


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