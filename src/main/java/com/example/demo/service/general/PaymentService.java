package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.*;

import java.util.List;

public interface PaymentService {
    ApiResponse<List<PaymentResponse>> getPaymentHistory(String userId);
    ApiResponse<List<TicketExchangeHistoryResponse>> getExchangeHistory(String userId);
    ApiResponse<TicketExchangeResponse> exchangeTickets(TicketExchangeRequest request);
}