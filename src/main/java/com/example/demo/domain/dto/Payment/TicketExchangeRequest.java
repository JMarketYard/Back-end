package com.example.demo.domain.dto.Payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketExchangeRequest {
    private String userId; // 사용자 ID
    private int quantity; // 환전 수량
    private String paymentMethod; // 환전 결제 수단

    public TicketExchangeRequest(String userId, int quantity, String paymentMethod) {
        this.userId = userId;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
    }

}