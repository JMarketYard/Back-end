package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.HomeConverter;
import com.example.demo.domain.dto.HomeResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final RaffleRepository raffleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public HomeResponseDTO getHome(){

        List<Raffle> raffles = raffleRepository.findAll();

        // 로그인한 회원이라고 가정
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));


        // 마감임박인 래플 5개 조회 (로그인 했을 시, 본인이 찜한 여부까지 같이 전달)
        LocalDateTime now = LocalDateTime.now();

        List<Raffle> rafflesSortedByEndAt = raffles.stream()
                .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                .sorted(Comparator.comparingLong(r -> Duration.between(now, r.getEndAt()).toMillis()))
                .limit(5)
                .toList();

        List<HomeResponseDTO.RaffleDTO> rafflesSortedByEndAtDTO = new ArrayList<>();

        for (Raffle raffle : rafflesSortedByEndAt) {
            HomeResponseDTO.RaffleDTO raffleDTO = HomeConverter.toHomeRaffleDTO(raffle);
            rafflesSortedByEndAtDTO.add(raffleDTO);
        }

        // 내가 찜한 래플 5개 조회 ( 로그인 했을 시 기능 )
        List<Like> sortedLikes = user.getLikes().stream()
                .sorted(Comparator.comparing(Like::getCreatedAt).reversed())
                .limit(5)
                .toList();

        List<HomeResponseDTO.RaffleDTO> myLikeRafflesDTO = new ArrayList<>();

        for (Like sortedLike : sortedLikes) {
            Raffle raffle = sortedLike.getRaffle();
            HomeResponseDTO.RaffleDTO raffleDTO = HomeConverter.toHomeRaffleDTO(raffle);
            myLikeRafflesDTO.add(raffleDTO);
        }

        // 내가 팔로우한 상점의 래플 5개 조회 (마감임박순, 로그인 했을 시 본인의 찜 여부도 전달)
        List<Follow> followings = user.getFollowings();
        List<Raffle> followingAllRaffles = new ArrayList<>();


        for (Follow following : followings) {
            List<Raffle> followingRaffles = following.getUser().getRaffles();
            followingAllRaffles.addAll(followingRaffles);
        }

        List<Raffle> myFollowRaffles = followingAllRaffles.stream()
                .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                .sorted(Comparator.comparingLong(r -> Duration.between(now, r.getEndAt()).toMillis()))
                .limit(5)
                .toList();

        List<HomeResponseDTO.RaffleDTO> myFollowingRafflesDTO = new ArrayList<>();

        for (Raffle raffle : myFollowRaffles) {
            HomeResponseDTO.RaffleDTO raffleDTO = HomeConverter.toHomeRaffleDTO(raffle);
            myFollowingRafflesDTO.add(raffleDTO);
        }


        // 래플 둘러보기 -> 응모자순으로 래플 조회 (응모 안마감된것 우선, 로그인 했을 시 찜 여부도 같이 전달)
        List<Raffle> rafflesSortedByApplyList = sortRafflesByApply(raffles, now);

        List<HomeResponseDTO.RaffleDTO> rafflesSortedByApplyListDTO = new ArrayList<>();

        for (Raffle raffle : rafflesSortedByApplyList) {
            HomeResponseDTO.RaffleDTO raffleDTO = HomeConverter.toHomeRaffleDTO(raffle);
            rafflesSortedByApplyListDTO.add(raffleDTO);
        }


        return getHomeResponseDTO(rafflesSortedByEndAtDTO, myLikeRafflesDTO, myFollowingRafflesDTO, rafflesSortedByApplyListDTO);
    }


    @Override
    public HomeResponseDTO getHomeCategories(String categoryName) {

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CustomException(ErrorStatus.COMMON_WRONG_PARAMETER));

        List<Raffle> raffles = raffleRepository.findByCategoryName(categoryName);
        LocalDateTime now = LocalDateTime.now();
        List<HomeResponseDTO.RaffleDTO> result = new ArrayList<>();

        // 카테고리별 조회 + 응모자순으로 래플 조회 (응모 안마감된것 우선)
        List<Raffle> rafflesSortedByApplyList = sortRafflesByApply(raffles, now);

        for (Raffle raffle : rafflesSortedByApplyList) {
            HomeResponseDTO.RaffleDTO homeRaffleDTO = HomeConverter.toHomeRaffleDTO(raffle);
            result.add(homeRaffleDTO);
        }

        return HomeResponseDTO.builder()
                .raffles(result).build();

    }


    /**
     * 사용하는 메소드 분리
    * */

    // 응모 마감 안된것 + 응모자순으로 정렬하는 로직
    private List<Raffle> sortRafflesByApply(List<Raffle> raffles, LocalDateTime now) {
        return Stream.concat(
                raffles.stream()
                        .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                        .sorted((r1, r2) -> Integer.compare(
                                r2.getApplyList() != null ? r2.getApplyList().size() : 0,
                                r1.getApplyList() != null ? r1.getApplyList().size() : 0
                        )),
                raffles.stream()
                        .filter(r -> Duration.between(now, r.getEndAt()).toMillis() < 0)
                        .sorted((r1, r2) -> Integer.compare(
                                r2.getApplyList() != null ? r2.getApplyList().size() : 0,
                                r1.getApplyList() != null ? r1.getApplyList().size() : 0
                        ))
        ).toList();
    }

    // HomeResponseDTO 만드는 메소드 분리
    private static HomeResponseDTO getHomeResponseDTO(List<HomeResponseDTO.RaffleDTO> rafflesSortedByEndAtDTO, List<HomeResponseDTO.RaffleDTO> myLikeRafflesDTO, List<HomeResponseDTO.RaffleDTO> myFollowingRafflesDTO, List<HomeResponseDTO.RaffleDTO> rafflesSortedByApplyListDTO) {
        return HomeResponseDTO.builder()
                .approaching(rafflesSortedByEndAtDTO)
                .myLikeRaffles(myLikeRafflesDTO)
                .myFollowRaffles(myFollowingRafflesDTO)
                .raffles(rafflesSortedByApplyListDTO)
                .build();
    }

}
