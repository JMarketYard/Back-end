package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.KakaoPayConverter;
import com.example.demo.domain.converter.UserPaymentConverter;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.entity.base.Payment.Payment;
import com.example.demo.entity.base.Payment.UserPayment;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserPaymentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.KakaoPayService;
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
import java.util.Map;
import java.util.Optional;

@Service
public class KakaoPayServiceImpl implements KakaoPayService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoPayServiceImpl.class);
    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final UserPaymentRepository userPaymentRepository;
    private final KakaoPayConverter kakaoPayConverter;
    private final UserPaymentConverter userPaymentConverter;
    private final String secretKey;
    public KakaoPayServiceImpl(
            @Value("${kakao.pay.cid}") String cid,
            @Value("${kakao.pay.secretKey}") String secretKey,
            @Value("${kakao.pay.approvalUrl}") String approvalUrl,
            @Value("${kakao.pay.cancelUrl}") String cancelUrl,
            @Value("${kakao.pay.failUrl}") String failUrl,
            PaymentRepository paymentRepository,
            UserPaymentRepository userPaymentRepository,
            KakaoPayConverter kakaoPayConverter,
            UserRepository userRepository
    ) {
        this.secretKey = secretKey;
        this.paymentRepository = paymentRepository;
        this.userPaymentRepository = userPaymentRepository;
        this.restTemplate = new RestTemplate();
        this.userPaymentConverter = new UserPaymentConverter();
        this.kakaoPayConverter = new KakaoPayConverter(cid, approvalUrl, cancelUrl, failUrl, userRepository);
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
        Map<String, Object> parameters = kakaoPayConverter.toPrepareParameters(paymentRequest);
        ReadyResponse readyResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                parameters,
                ReadyResponse.class
        );

        Optional.ofNullable(readyResponse).ifPresent(response -> {
            Payment paymentEntity = kakaoPayConverter.toEntity(paymentRequest, response);
            savePaymentEntity(paymentEntity);
        });

        return ApiResponse.of(SuccessStatus.PAYMENT_READY_SUCCESS, readyResponse);
    }

    @Override
    public ApiResponse<ApproveResponse> approvePayment(String pgToken, String tid) {
        Payment payment = findPaymentByTid(tid);
        ApproveResponse approveResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                kakaoPayConverter.toApproveParameters(payment, pgToken),
                ApproveResponse.class
        );
        Optional.ofNullable(approveResponse).ifPresent(response -> {
            payment.setStatus("APPROVED");
            payment.setApprovedAt(LocalDateTime.now());
            savePaymentEntity(payment);
        });

        // 유저 티켓 수 증가 또는 유저 생성 처리
        String userId = payment.getUserId();
        UserPayment userPayment = userPaymentRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // 유저가 없을 경우, UserPaymentConverter를 사용하여 새 UserPayment 생성
                    return userPaymentConverter.createDefaultUserPayment(userId);
                });

        // 티켓 수 업데이트
        if (!payment.getItemId().equals("배송비")) {
            int updatedTickets = Integer.parseInt(userPayment.getUserTicket()) + payment.getAmount();
            userPayment.setUserTicket(String.valueOf(updatedTickets));
            userPayment.setUpdatedAt(LocalDateTime.now());
        }

        // 변경된 유저 정보 저장
        userPaymentRepository.save(userPayment);

        return ApiResponse.of(SuccessStatus.PAYMENT_APPROVE_SUCCESS, approveResponse);
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