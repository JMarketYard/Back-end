package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.service.general.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

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

    @PostMapping("/cancel")
    public ApiResponse<CancelResponse> cancelPayment(@RequestParam String tid) {
        return paymentService.cancelPayment(tid);
    }
}