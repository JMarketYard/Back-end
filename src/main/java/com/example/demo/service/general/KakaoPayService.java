package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.*;

public interface KakaoPayService {
    ApiResponse<ReadyResponse> preparePayment(PaymentRequest paymentRequest);
    ApiResponse<ApproveResponse> approvePayment(String pgToken, String tid);
}