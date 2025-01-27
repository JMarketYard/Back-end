package com.example.demo.repository;

import com.example.demo.entity.base.Payment.TicketExchange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketExchangeRepository extends JpaRepository<TicketExchange, Long> {

    // 사용자 ID로 티켓 환전 내역을 조회하고, 환전일 기준 내림차순으로 정렬
    Page<TicketExchange> findByUserIdOrderByExchangeDateDesc(String userId, Pageable pageable);

    // 환전 ID로 티켓 환전 정보를 조회
    TicketExchange findById(long id);
}
