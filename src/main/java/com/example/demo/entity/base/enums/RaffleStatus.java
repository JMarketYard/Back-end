package com.example.demo.entity.base.enums;

public enum RaffleStatus {
    UNOPENED,    // 응모 시작 전
    ACTIVE,     // 응모 중
    EXPIRED,    // 최소 티켓 수 미충족 종료
    ENDED       // 응모 마감 및 당첨자 추첨 완료
}
