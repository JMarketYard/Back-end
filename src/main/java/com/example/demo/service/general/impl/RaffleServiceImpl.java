package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.dto.Raffle.RaffleRequestDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.domain.converter.RaffleConverter.toApplyDto;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RaffleServiceImpl implements RaffleService {

    private final RaffleRepository raffleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RaffleSchedulerService raffleSchedulerService;
    private final S3UploadService s3UploadService;
    private final ImageService imageService;
    private final ApplyRepository applyRepository;

    @Override
    @Transactional
    public RaffleResponseDTO.UploadResultDTO uploadRaffle(RaffleRequestDTO.UploadDTO request) {

        // 0. 업로드 작성자 정보 가져오기 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 1. 요청받은 카테고리 이름으로 Category 엔티티 가져오기
        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new CustomException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 2. S3에 파일 업로드
        List<String> imageUrls = s3UploadService.saveFile(request.getFiles());

        // 3. 래플 생성
        Raffle raffle = RaffleConverter.toRaffle(request, category ,user);

        // 4. 이미지 엔티티 생성
        List<Image> images = imageService.saveImages(imageUrls);

        // 5. 래플과 이미지 엔티티 매핑
        images.forEach(img -> raffle.addImage(img));

        // 6. 래플 저장
        raffleRepository.save(raffle);

        raffleSchedulerService.scheduleRaffleJob(raffle, true);
        raffleSchedulerService.scheduleRaffleJob(raffle, false);

        // 7. 래플 엔티티를 ResponseDTO로 변환 후 반환
        return RaffleConverter.toUploadResultDTO(raffle);
    }

    @Override
    @Transactional
    public RaffleResponseDTO.RaffleDetailDTO getRaffleDetailsDTO(Long raffleId) {

        // 1. 요청받은 래플 id로 엔티티 조회
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        Long userId = raffle.getUser().getId(); // userId 조회

        // 2. 필요 데이터 조회 (쿼리 4개 날아가서 추후 개선 예정)
        int likeCount, applyCount, followCount, reviewCount;
        likeCount = raffleRepository.countLikeByRaffleId(raffleId);
        applyCount = raffleRepository.countApplyByRaffleId(raffleId);
        followCount = raffleRepository.countFollowsByUserId(userId);
        reviewCount = raffleRepository.countReviewsByUserId(userId);

        // 3. 조회 수 증가
        raffle.addView();

        // 4. DTO 변환 및 반환
        return RaffleConverter.toDetailDTO(raffle, likeCount, applyCount, followCount, reviewCount);
    }

    @Override
    @Transactional
    public RaffleResponseDTO.ApplyDTO apply(Long raffleId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        User user = userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        int userTicket = user.getTicket_num();

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));
        int raffleTicket = raffle.getTicketNum();

        if (raffle.getRaffleStatus() == RaffleStatus.UNOPENED)
            throw new CustomException(ErrorStatus.APPLY_UNOPENED_RAFFLE);
        if (raffle.getRaffleStatus() != RaffleStatus.ACTIVE)
            throw new CustomException(ErrorStatus.APPLY_FINISHED_RAFFLE);

        if (raffle.getUser().equals(user))
            throw new CustomException(ErrorStatus.APPLY_SELF_RAFFLE);

        if (applyRepository.existsByRaffleAndUser(raffle, user))
            throw new CustomException(ErrorStatus.APPLY_ALREADY_APPLIED);

        if (userTicket < raffleTicket)
            throw new CustomException(ErrorStatus.APPLY_INSUFFICIENT_TICKET);

        user.setTicket_num(userTicket - raffleTicket);
        userRepository.save(user);

        Apply apply = Apply.builder()
                .raffle(raffle)
                .user(user)
                .build();
        applyRepository.save(apply);

        return toApplyDto(apply);
    }
}
