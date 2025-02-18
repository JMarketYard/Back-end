package com.example.demo.domain.dto.Payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExchangeHistoryResponse {

    private String exchangeId;
    private LocalDateTime exchangedDate; // 환전 일자
    private int user_ticket; // 현재 유저 티켓
    private String exchangeMethod; // 환전 수단
    private int amount; // 환전 금액

    public ExchangeHistoryResponse(Long exchangeId, LocalDateTime exchangedDate, int user_ticket, String exchangeMethod, int amount) {
        this.exchangeId = exchangeId.toString();
        this.exchangedDate = exchangedDate;
        this.user_ticket = user_ticket;
        this.exchangeMethod = exchangeMethod;
        this.amount = amount;
    }

}