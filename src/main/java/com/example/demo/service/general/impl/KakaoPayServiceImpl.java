package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.PaymentConverter;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.entity.Payment;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KakaoPayServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoPayServiceImpl.class);
    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final PaymentConverter paymentConverter;
    private final String secretKey;
    public KakaoPayServiceImpl(
            @Value("${kakao.pay.cid}") String cid,
            @Value("${kakao.pay.secretKey}") String secretKey,
            @Value("${kakao.pay.approvalUrl}") String approvalUrl,
            @Value("${kakao.pay.cancelUrl}") String cancelUrl,
            @Value("${kakao.pay.failUrl}") String failUrl,
            PaymentRepository paymentRepository,
            UserRepository userRepository
    ) {
        this.secretKey = secretKey;
        this.paymentRepository = paymentRepository;
        this.restTemplate = new RestTemplate();
        this.paymentConverter = new PaymentConverter(cid, approvalUrl, cancelUrl, failUrl, userRepository);
    }
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    private <T> T sendRequest(String url, Map<String, Object> parameters, Class<T> responseType) {
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, createHeaders());
            ResponseEntity<T> response = restTemplate.postForEntity(url, requestEntity, responseType);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                logger.error("API 요청 실패: 상태 코드 - {}", response.getStatusCode());
                throw new CustomException(ErrorStatus.PAYMENT_REQUEST_FAILED);
            }
        } catch (Exception e) {
            logger.error("API 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_REQUEST_ERROR);
        }
    }

    @Override
    public ApiResponse<ReadyResponse> preparePayment(PaymentRequest paymentRequest) {
        Map<String, Object> parameters = paymentConverter.toPrepareParameters(paymentRequest);
        ReadyResponse readyResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                parameters,
                ReadyResponse.class
        );

        Optional.ofNullable(readyResponse).ifPresent(response -> {
            Payment paymentEntity = paymentConverter.toEntity(paymentRequest, response);
            savePaymentEntity(paymentEntity);
        });

        return ApiResponse.of(SuccessStatus.PAYMENT_READY_SUCCESS, readyResponse);
    }

    @Override
    public ApiResponse<ApproveResponse> approvePayment(String pgToken, String tid) {
        Payment payment = findPaymentByTid(tid);
        ApproveResponse approveResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                paymentConverter.toApproveParameters(payment, pgToken),
                ApproveResponse.class
        );
        Optional.ofNullable(approveResponse).ifPresent(response -> {
            payment.setStatus("APPROVED");
            payment.setApprovedAt(LocalDateTime.now());
            savePaymentEntity(payment);
        });

        return ApiResponse.of(SuccessStatus.PAYMENT_APPROVE_SUCCESS, approveResponse);
    }
    @Override
    public ApiResponse<CancelResponse> cancelPayment(String tid) {
        Payment payment = findPaymentByTid(tid);
        CancelResponse cancelResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/cancel",
                paymentConverter.toCancelParameters(payment),
                CancelResponse.class
        );
        payment.setStatus("CANCELLED");
        savePaymentEntity(payment);
        return ApiResponse.of(SuccessStatus.PAYMENT_CANCEL_SUCCESS, cancelResponse);
    }
    @Override
    public ApiResponse<List<PaymentResponse>> getPaymentHistory(String userId) {
        try {
            List<PaymentResponse> paymentHistory = paymentRepository.findByUserId(userId).stream()
                    .map(paymentConverter::toDto)
                    .collect(Collectors.toList());
            return ApiResponse.of(SuccessStatus.PAYMENT_HISTORY_SUCCESS, paymentHistory);
        } catch (Exception e) {
            logger.error("결제 내역 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_HISTORY_ERROR);
        }
    }
    private Payment findPaymentByTid(String tid) {
        return paymentRepository.findByTid(tid)
                .orElseThrow(() -> new CustomException(ErrorStatus.PAYMENT_NOT_FOUND));
    }
    private void savePaymentEntity(Payment paymentEntity) {
        try {
            paymentRepository.save(paymentEntity);
        } catch (Exception e) {
            logger.error("결제 정보 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_SAVE_FAILED);
        }
    }

}