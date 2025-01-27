package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.service.general.KakaoPayService;
import com.example.demo.service.general.PaymentService;
import com.example.demo.service.general.UserPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final KakaoPayService kakaoPayService;
    private final PaymentService paymentService;
    private final UserPaymentService userPaymentService;

    @PostMapping("/ready")
    public ApiResponse<ReadyResponse> preparePayment(@RequestBody PaymentRequest paymentRequest) {
        return kakaoPayService.preparePayment(paymentRequest);
    }

    @GetMapping("/approve")
    public ApiResponse<ApproveResponse> approvePayment(@RequestParam String pgToken, @RequestParam String tid) {
        return kakaoPayService.approvePayment(pgToken, tid);
    }

    @GetMapping("/tickets/{userId}")
    public ApiResponse<UserTicketResponse> getUserTickets(@PathVariable String userId) {
        return userPaymentService.getUserTickets(userId);
    }

    @PostMapping("/bankInfo/{userId}")
    public ApiResponse<UserBankInfoResponse> getUserPaymentInfo(@PathVariable String userId, @RequestBody UserBankInfoRequest userBankInfoRequest) {
        return userPaymentService.getUserPaymentInfo(userId, userBankInfoRequest);
    }

    @GetMapping("/history/charge/{userId}")
    public ApiResponse<List<PaymentResponse>> getPaymentHistory(@PathVariable String userId) {
        return paymentService.getPaymentHistory(userId);
    }

    @GetMapping("/history/exchange/{userId}")
    public ApiResponse<List<TicketExchangeHistoryResponse>> getTicketExchangeHistory(@PathVariable String userId) {
        return paymentService.getExchangeHistory(userId);
    }

    @PostMapping("/exchange")
    public ApiResponse<TicketExchangeResponse> exchangeTickets(@RequestBody TicketExchangeRequest request) {
        return paymentService.exchangeTickets(request);
    }

}