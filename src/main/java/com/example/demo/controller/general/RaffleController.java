package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.dto.RaffleRequestDTO;
import com.example.demo.domain.dto.RaffleResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.RaffleService;
import com.example.demo.service.general.S3UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/permit")
public class RaffleController {

    private final RaffleService raffleService;
    private final S3UploadService s3UploadService;

    @PostMapping(value = "/raffles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RaffleResponseDTO.UploadResultDTO> upload(@ModelAttribute @Valid RaffleRequestDTO.UploadDTO request) {

        // 1. raffle 업로드 처리 : 서비스 계층에 요청
        RaffleResponseDTO.UploadResultDTO uploadResultDTO = raffleService.uploadRaffle(request);

        // 2.성공 응답 + 업로드 결과 DTO 반환
        return ApiResponse.of(SuccessStatus.RAFFLE_UPLOAD_SUCCESS, uploadResultDTO);
    }

    @GetMapping("/raffles/{raffleId}")
    public ApiResponse<RaffleResponseDTO.RaffleDetailDTO> getPostById(@PathVariable Long raffleId) {

        // 1. 래플id로 해당 detailDTO 받아오기
        RaffleResponseDTO.RaffleDetailDTO raffleDetailDTO = raffleService.getRaffleDetailsDTO(raffleId);

        // 2. 성공 응답 + 해당 detailDTO 반환
        return ApiResponse.of(SuccessStatus.RAFFLE_FETCH_SUCCESS, raffleDetailDTO);
    }

    // 연습

//    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ApiResponse<String> uploadImage(
//            @RequestParam("file") MultipartFile file) throws Exception {
//        String fileUrl = s3UploadService.saveFile(file);
//        return ApiResponse.of(SuccessStatus.IMAGE_UPLOAD_SUCCESS, fileUrl);
//    }

}
