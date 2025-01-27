package com.example.demo.entity.base.Payment;

import com.example.demo.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TicketExchange extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 고유 식별자 (기본 키)
    private String userId;  // 사용자 ID (환전 요청한 사용자)
    private int quantity;  // 환전 수량 (사용자가 요청한 티켓의 수량)
    private String paymentMethod;  // 결제 수단 (카드, 페이팔 등)
    private int amount;  // 환전 금액 (수량 * 금액 단위)
    private String bankName;  // 은행 이름 (예: "신한은행")
    private String accountNumber;  // 계좌 번호 (환전 금액을 이체할 계좌 번호)
    private LocalDateTime exchangeDate;  // 환전 처리 날짜
    private LocalDateTime exchangedAt;  // 환전 완료 날짜

    public TicketExchange() {}

    public TicketExchange(String userId, int quantity, String paymentMethod, int amount, String bankName, String accountNumber, LocalDateTime exchangeDate) {
        this.userId = userId;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.exchangeDate = exchangeDate;
    }

}