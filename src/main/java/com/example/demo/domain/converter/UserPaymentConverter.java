package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Payment.UserBankInfoRequest;
import com.example.demo.entity.base.Payment.UserPayment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserPaymentConverter {

    public UserPayment createDefaultUserPayment(String userId) {
        UserPayment userPayment = new UserPayment();
        userPayment.setUserId(userId);
        userPayment.setUserTicket("0"); // 기본 티켓 수 0으로 설정
        userPayment.setBankName("Default Bank"); // 기본 은행 이름 설정
        userPayment.setBankNumber("000-000-0000"); // 기본 계좌번호 설정
        userPayment.setUpdatedAt(LocalDateTime.now());
        return userPayment;
    }
    public void updateUserPaymentWithBankInfo(UserPayment userPayment, UserBankInfoRequest request) {
        userPayment.setBankName(request.getBankName());
        userPayment.setBankNumber(request.getBankNumber());
        userPayment.setUpdatedAt(LocalDateTime.now()); // 업데이트 시간 갱신
    }
}
