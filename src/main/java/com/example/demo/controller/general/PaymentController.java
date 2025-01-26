package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.service.general.PaymentService;
import com.example.demo.service.general.UserPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserPaymentService userPaymentService;

    @PostMapping("/ready")
    public ApiResponse<ReadyResponse> preparePayment(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.preparePayment(paymentRequest);
    }

    @GetMapping("/approve")
    public ApiResponse<ApproveResponse> approvePayment(@RequestParam String pgToken, @RequestParam String tid) {
        return paymentService.approvePayment(pgToken, tid);
    }

    @GetMapping("/history/{userId}")
    public ApiResponse<List<PaymentResponse>> getPaymentHistory(@PathVariable String userId) {
        return paymentService.getPaymentHistory(userId);
    }

    @GetMapping("/tickets/{userId}")
    public ApiResponse<UserTicketResponse> getUserTickets(@PathVariable String userId) {
        return userPaymentService.getUserTickets(userId);
    }

    @PostMapping("/bankInfo/{userId}")
    public ApiResponse<UserBankInfoResponse> getUserPaymentInfo(@PathVariable String userId, @RequestBody UserBankInfoRequest userBankInfoRequest) {
        return userPaymentService.getUserPaymentInfo(userId, userBankInfoRequest);
    }

}