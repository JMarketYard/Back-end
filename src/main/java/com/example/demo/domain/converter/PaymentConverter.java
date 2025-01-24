package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Payment.PaymentRequest;
import com.example.demo.domain.dto.Payment.ReadyResponse;
import com.example.demo.entity.Payment;
import com.example.demo.domain.dto.Payment.PaymentResponse;
import com.example.demo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PaymentConverter {
    private final String cid;
    private final String approvalUrl;
    private final String cancelUrl;
    private final String failUrl;
    private final UserRepository userRepository;
    public PaymentConverter(String cid, String approvalUrl, String cancelUrl, String failUrl, UserRepository userRepository) {
        this.cid = cid;
        this.approvalUrl = approvalUrl;
        this.cancelUrl = cancelUrl;
        this.failUrl = failUrl;
        this.userRepository = userRepository;
    }
    public Map<String, Object> toPrepareParameters(PaymentRequest paymentRequest) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", this.cid);
        parameters.put("partner_user_id", paymentRequest.getUserId());
        parameters.put("partner_order_id", paymentRequest.getOrderId());
        parameters.put("item_name", paymentRequest.getItemName());
        parameters.put("quantity", paymentRequest.getQuantity());
        parameters.put("total_amount", paymentRequest.getTotalAmount());
        parameters.put("tax_free_amount", 0);
        parameters.put("approval_url", this.approvalUrl);
        parameters.put("cancel_url", this.cancelUrl);
        parameters.put("fail_url", this.failUrl);
        return parameters;
    }

    public Map<String, Object> toApproveParameters(Payment payment, String pgToken) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", this.cid);
        parameters.put("tid", payment.getTid());
        parameters.put("partner_user_id", payment.getUserId());
        parameters.put("partner_order_id", payment.getOrderId());
        parameters.put("pg_token", pgToken);
        return parameters;
    }

    public Map<String, Object> toCancelParameters(Payment payment) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", this.cid);
        parameters.put("tid", payment.getTid());
        parameters.put("cancel_amount", payment.getAmount());
        parameters.put("cancel_tax_free_amount", 0);
        return parameters;
    }

    public Payment toEntity(PaymentRequest paymentRequest, ReadyResponse readyResponse) {
        Payment payment = new Payment();
        payment.setUserId(paymentRequest.getUserId());
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setItemId(paymentRequest.getItemId());
        payment.setItemName(paymentRequest.getItemName());
        payment.setQuantity(paymentRequest.getQuantity());
        payment.setAmount(paymentRequest.getTotalAmount());
        payment.setTid(readyResponse.getTid());
        payment.setStatus("READY");
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }

    public PaymentResponse toDto(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setUserId(payment.getUserId());
        response.setOrderId(payment.getOrderId());
        response.setItemId(payment.getItemId());
        response.setItemName(payment.getItemName());
        response.setQuantity(payment.getQuantity());
        response.setAmount(payment.getAmount());
        response.setTid(payment.getTid());
        response.setStatus(payment.getStatus());
        response.setCreatedAt(payment.getCreatedAt());
        response.setApprovedAt(payment.getApprovedAt());
        return response;
    }
}