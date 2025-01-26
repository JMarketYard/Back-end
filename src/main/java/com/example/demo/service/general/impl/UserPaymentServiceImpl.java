package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.UserPaymentConverter;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.entity.UserPayment;
import com.example.demo.repository.UserPaymentRepository;
import com.example.demo.service.general.UserPaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserPaymentServiceImpl implements UserPaymentService {

    private final UserPaymentRepository userPaymentRepository;
    private final UserPaymentConverter userPaymentConverter;
    public UserPaymentServiceImpl (UserPaymentRepository userPaymentRepository, UserPaymentConverter userPaymentConverter) {
        this.userPaymentRepository = userPaymentRepository;
        this.userPaymentConverter = userPaymentConverter;
    }

    @Override
    public ApiResponse<UserTicketResponse> getUserTickets(String userId) {
        Optional<UserPayment> userPayment = userPaymentRepository.findByUserId(userId);

        // 유저가 존재하지 않으면 에러 반환
        if (userPayment.isEmpty()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }

        int ticketCount = Integer.parseInt(userPayment.get().getUserTicket());
        LocalDateTime updatedAt = userPayment.get().getUpdatedAt();
        UserTicketResponse response = new UserTicketResponse();
        response.setTicket(ticketCount);
        response.setUpdatedAt(updatedAt);

        return ApiResponse.of(SuccessStatus.USER_PAYMENT_GET_TICKET, response);
    }

    @Override
    public ApiResponse<UserBankInfoResponse> getUserPaymentInfo(String userId, UserBankInfoRequest request) {
        // 유저 정보 조회
        UserPayment userPayment = userPaymentRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // 유저가 없으면 새로 생성
                    UserPayment newUserPayment = userPaymentConverter.createDefaultUserPayment(userId);
                    userPaymentRepository.save(newUserPayment); // 새로 생성된 유저 정보 저장
                    return newUserPayment; // 생성된 유저 정보 반환
                });

        // UserPayment를 업데이트
        userPaymentConverter.updateUserPaymentWithBankInfo(userPayment, request);
        userPaymentRepository.save(userPayment); // 업데이트된 정보 저장

        // 응답 객체 생성
        UserBankInfoResponse response = new UserBankInfoResponse();
        response.setBankName(userPayment.getBankName());
        response.setBankNumber(userPayment.getBankNumber());

        return ApiResponse.of(SuccessStatus.USER_PAYMENT_UPDATE_BANK_INFO, response);

    }

    /*@Override
    public ApiResponse<RafflePaymentResponse> payRaffle(RafflePaymentRequest request) {

    }*/

}