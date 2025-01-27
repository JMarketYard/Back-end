package com.example.demo.repository;

import com.example.demo.entity.base.Payment.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 사용자 ID로 결제 내역을 조회하고, 승인 시간을 기준으로 내림차순 정렬하여 페이징 처리
    Page<Payment> findByUserIdOrderByApprovedAtDesc(String userId, Pageable pageable);

    // 결제 ID(TID)로 결제 정보를 조회
    Optional<Payment> findByTid(String tid);

    // 사용자 ID로 결제 내역을 조회
    List<Payment> findByUserId(String userId);
}
