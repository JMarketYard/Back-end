package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Raffle.RaffleRequestDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Image;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

import java.util.List;

public class RaffleConverter {

    public static Raffle toRaffle(RaffleRequestDTO.UploadDTO request, Category category, User user) {

        return Raffle.builder()
                .user(user)
                .winner(null)
                .category(category)
                .name(request.getName())
                .status(request.getStatus())
                .description(request.getDescription())
                .ticketNum(request.getTicketNum())
                .minTicket(request.getMinTicket())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .build();
    }

    public static RaffleResponseDTO.UploadResultDTO toUploadResultDTO(Raffle raffle) {
        return RaffleResponseDTO.UploadResultDTO.builder()
                .raffle_id(raffle.getId())
                .build();
    }

    public static RaffleResponseDTO.RaffleDetailDTO toDetailDTO(Raffle raffle) {
        return RaffleResponseDTO.RaffleDetailDTO.builder()
                .imageUrls(raffle.getImages().stream().map(Image::getImageUrl).toList()) // 이미지 url
                .name(raffle.getName()) // 상품명
                .description(raffle.getDescription()) // 상품설명
                .category(raffle.getCategory().getName()) // 카테고리명
                .ticketNum(raffle.getTicketNum()) // 응모에 필요한 티켓 수
                .startAt(raffle.getStartAt()) // 응모 오픈
                .endAt(raffle.getEndAt()) // 응모 마감
                .view(raffle.getView()) // 조회
                .likeCount(raffle.getLikeCount()) // 찜

                .minTicket(raffle.getMinTicket()) // 최소 티켓 수
                // 현재 참여자 수 필요

                .nickname(raffle.getUser().getNickname()) // 판매자 닉네임
                // 팔로우 수, 후기 수 필요
                .build();
    }
}
