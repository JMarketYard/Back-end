package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.*;

import java.util.List;

public interface PaymentService {
    ApiResponse<ReadyResponse> preparePayment(PaymentRequest paymentRequest);
    ApiResponse<ApproveResponse> approvePayment(String pgToken, String tid);
    ApiResponse<CancelResponse> cancelPayment(String tid);
    ApiResponse<List<PaymentResponse>> getPaymentHistory(String userId);
}