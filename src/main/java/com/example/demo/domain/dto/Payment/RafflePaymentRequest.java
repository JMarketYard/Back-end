package com.example.demo.domain.dto.Payment;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RafflePaymentRequest {
    private String userId;
    private String orderId;
    private String itemId;
    private String itemName;
    private int quantity;
    private int ticketAmount;
}