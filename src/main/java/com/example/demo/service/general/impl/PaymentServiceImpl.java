package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Payment.PaymentResponse;
import com.example.demo.domain.dto.Payment.TicketExchangeHistoryResponse;
import com.example.demo.domain.dto.Payment.TicketExchangeRequest;
import com.example.demo.domain.dto.Payment.TicketExchangeResponse;
import com.example.demo.entity.base.Payment.Payment;
import com.example.demo.entity.base.Payment.TicketExchange;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.TicketExchangeRepository;
import com.example.demo.service.general.PaymentService;
import com.example.demo.domain.converter.PaymentConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final PaymentConverter paymentConverter;
    private final TicketExchangeRepository ticketExchangeRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentConverter paymentConverter,
                              TicketExchangeRepository ticketExchangeRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentConverter = paymentConverter;
        this.ticketExchangeRepository = ticketExchangeRepository;
    }

    @Override
    public ApiResponse<List<PaymentResponse>> getPaymentHistory(String userId) {
        try {
            Pageable pageable = PageRequest.of(0, 50); // 50개씩 페이징
            Page<Payment> payments = paymentRepository.findByUserIdOrderByApprovedAtDesc(userId, pageable);

            List<PaymentResponse> paymentHistory = payments.stream()
                    .map(paymentConverter::toPaymentResponse)
                    .collect(Collectors.toList());

            return ApiResponse.of(SuccessStatus.PAYMENT_HISTORY_SUCCESS, paymentHistory);
        } catch (Exception e) {
            logger.error("결제 내역 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_HISTORY_ERROR);
        }
    }

    @Override
    public ApiResponse<List<TicketExchangeHistoryResponse>> getExchangeHistory(String userId) {
        try {
            Pageable pageable = PageRequest.of(0, 50); // 50개씩 페이징
            Page<TicketExchange> exchanges = ticketExchangeRepository.findByUserIdOrderByExchangeDateDesc(userId, pageable);

            List<TicketExchangeHistoryResponse> historyList = exchanges.stream()
                    .map(paymentConverter::toTicketExchangeHistoryResponse)
                    .collect(Collectors.toList());

            return ApiResponse.of(SuccessStatus.EXCHANGE_HISTORY_SUCCESS, historyList);
        } catch (Exception e) {
            logger.error("티켓 환전 내역 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.EXCHANGE_HISTORY_ERROR);
        }
    }

    @Override
    public ApiResponse<TicketExchangeResponse> exchangeTickets(TicketExchangeRequest request) {
        try {
            // 요청 정보를 기반으로 엔티티 생성
            TicketExchange ticketExchange = paymentConverter.toTicketExchangeEntity(request);
            ticketExchange.setExchangedAt(LocalDateTime.now());

            // 데이터베이스에 저장
            ticketExchangeRepository.save(ticketExchange);

            // 결과 DTO 생성
            TicketExchangeResponse response = paymentConverter.toTicketExchangeResponse(ticketExchange);

            return ApiResponse.of(SuccessStatus.EXCHANGE_SUCCESS, response);
        } catch (Exception e) {
            logger.error("티켓 환전 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.EXCHANGE_ERROR);
        }
    }
}
