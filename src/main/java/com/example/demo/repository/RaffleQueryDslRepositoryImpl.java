package com.example.demo.repository;

import com.example.demo.entity.QApply;
import com.example.demo.entity.QLike;
import com.example.demo.entity.QRaffle;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.RaffleSortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RaffleQueryDslRepositoryImpl implements RaffleQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Raffle> findRafflesWithSorting(Pageable pageable, RaffleSortType sortType, Boolean raffleNotEnded, String categoryName) {
        QRaffle raffle = QRaffle.raffle;
        QApply apply = QApply.apply;
        QLike like = QLike.like;
        LocalDateTime now = LocalDateTime.now();

        JPAQuery<Raffle> query = queryFactory
                .selectFrom(raffle);

        // 응모자순일 때 응모 테이블 조인
        if (sortType == RaffleSortType.MOST_APPLIED) {
            query.leftJoin(apply).on(apply.raffle.eq(raffle)).groupBy(raffle.id);
        }
        // 찜순일 때 찜 테이블 조인
        else if (sortType == RaffleSortType.MOST_LIKED) {
            query.leftJoin(like).on(like.raffle.eq(raffle)).groupBy(raffle.id);
        }

        // 마감된 래플 안보고 싶을때 필터
        if (raffleNotEnded) {
            query.where(raffle.endAt.after(now));
        }

        // 카테고리 필터
        if (categoryName != null && !categoryName.isBlank()) {
            query.where(raffle.category.name.eq(categoryName));
        }

        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        switch (sortType) {
            case ENDING_SOON:
                query.where(raffle.endAt.after(now));
                orderSpecifiers.add(raffle.endAt.asc());
                break;
            case MOST_APPLIED:
                orderSpecifiers.add(apply.count().desc());
                break;
            case RECENTLY_UPLOADED:
                orderSpecifiers.add(raffle.createdAt.desc());
                break;
            case MOST_LIKED:
                orderSpecifiers.add(like.count().desc());
                orderSpecifiers.add(like.id.count().desc());
                break;
        }

        query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Raffle> content = query.fetch();

        Long total = queryFactory
                .select(raffle.countDistinct())
                .from(raffle)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
