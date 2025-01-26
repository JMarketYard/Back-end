package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.*;

public interface UserPaymentService {
    ApiResponse<UserTicketResponse> getUserTickets(String userId);
    ApiResponse<UserBankInfoResponse> getUserPaymentInfo(String userId, UserBankInfoRequest userBankInfoRequest);
    // ApiResponse<RafflePaymentResponse> payRaffle(RafflePaymentRequest rafflePaymentRequest);
}