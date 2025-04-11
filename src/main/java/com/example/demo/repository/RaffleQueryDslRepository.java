package com.example.demo.repository;

import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.RaffleSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface RaffleQueryDslRepository {
    Page<Raffle> findRafflesWithSorting(Pageable pageable, RaffleSortType sortType, Boolean raffleNotEnded, String categoryName);
}
