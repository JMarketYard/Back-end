package com.example.demo.domain.dto.Payment;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Getter
@Setter
public class PaymentResponse {
    private Long id;
    private String userId;
    private String orderId;
    private String itemId;
    private String itemName;
    private int quantity;
    private String tid;
    private int amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}