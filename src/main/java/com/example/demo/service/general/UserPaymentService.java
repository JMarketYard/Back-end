package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.*;

import java.util.List;

public interface UserPaymentService {
    ApiResponse<UserTicketResponse> getUserTickets(String userId);
    ApiResponse<UserBankInfoResponse> postUserPaymentInfo(String userId, UserBankInfoRequest userBankInfoRequest);
    ApiResponse<UserBankInfoResponse> getUserPaymentInfo(String userId);
    ApiResponse<List<PaymentResponse>> getPaymentHistory(String userId, String period);
    ApiResponse<Void> tradeTickets(String userId, String role, int ticketCount);
}