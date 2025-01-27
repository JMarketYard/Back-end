package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Payment.PaymentResponse;
import com.example.demo.domain.dto.Payment.TicketExchangeHistoryResponse;
import com.example.demo.domain.dto.Payment.TicketExchangeRequest;
import com.example.demo.domain.dto.Payment.TicketExchangeResponse;
import com.example.demo.entity.base.Payment.Payment;
import com.example.demo.entity.base.Payment.TicketExchange;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentConverter {

    // Converts Payment entity to PaymentResponse DTO
    public PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getApprovedAt(),
                payment.getQuantity(),
                "카카오페이",
                payment.getAmount()
        );
    }

    // Converts TicketExchange entity to TicketExchangeHistoryResponse DTO
    public TicketExchangeHistoryResponse toTicketExchangeHistoryResponse(TicketExchange ticketExchange) {
        return new TicketExchangeHistoryResponse(
                ticketExchange.getId(),
                ticketExchange.getExchangeDate(),
                ticketExchange.getQuantity(),
                ticketExchange.getPaymentMethod(),
                ticketExchange.getAmount()
        );
    }

    // Converts TicketExchangeRequest DTO to TicketExchange entity
    public TicketExchange toTicketExchangeEntity(TicketExchangeRequest request) {
        TicketExchange ticketExchange = new TicketExchange();
        ticketExchange.setUserId(request.getUserId());
        ticketExchange.setQuantity(request.getQuantity());
        ticketExchange.setPaymentMethod(request.getPaymentMethod());
        ticketExchange.setExchangeDate(LocalDateTime.now());  // Set current date-time for the exchange
        ticketExchange.setAmount(calculateExchangeAmount(request.getQuantity())); // Assuming there's a method to calculate the amount based on quantity
        return ticketExchange;
    }

    // Converts TicketExchange entity to TicketExchangeResponse DTO
    public TicketExchangeResponse toTicketExchangeResponse(TicketExchange ticketExchange) {
        return new TicketExchangeResponse(
                ticketExchange.getId(),
                ticketExchange.getExchangeDate(),
                ticketExchange.getQuantity(),
                ticketExchange.getPaymentMethod(),
                ticketExchange.getAmount()
        );
    }

    // Sample method to calculate the exchange amount (adjust based on business logic)
    private int calculateExchangeAmount(int quantity) {
        int exchangeRate = 100;  // Example rate
        return quantity * exchangeRate;
    }
}
