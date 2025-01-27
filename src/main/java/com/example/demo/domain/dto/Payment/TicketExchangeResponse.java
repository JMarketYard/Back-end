package com.example.demo.domain.dto.Payment;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketExchangeResponse {

    private String exchangeId;  // 환전 ID
    private LocalDateTime exchangeDate;  // 환전 날짜
    private int quantity;  // 환전 수량
    private String paymentMethod;  // 결제 수단
    private int amount;  // 환전 금액

    // 생성자
    public TicketExchangeResponse(Long exchangeId, LocalDateTime exchangeDate, int quantity, String paymentMethod, int amount) {
        this.exchangeId = String.valueOf(exchangeId);
        this.exchangeDate = exchangeDate;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }
}
