package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.service.general.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequestMapping("/api/permit/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/applies")
    public ApiResponse<MypageResponseDTO.ApplyListDto> getApplies(){

        return ApiResponse.of(_OK, mypageService.getApplies());

    }

    @Operation(summary = "등록된 주소 조회하기")
    @GetMapping("/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> getAddresses(Authentication authentication){

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, mypageService.getAddresses(Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "기본 배송지 설정하기")
    @PostMapping("/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> setDefault(
            @RequestBody MypageRequestDTO.AddressDto addressDto, Authentication authentication){

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, mypageService.setDefault(addressDto,Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "주소 추가하기")
    @PostMapping("/setting/addresses/add")
    public ApiResponse<?> addAddress(
            @RequestBody MypageRequestDTO.AddressAddDto addressAddDto, Authentication authentication){

        if(authentication == null || !authentication.isAuthenticated())
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);

        mypageService.addAddress(addressAddDto, Long.parseLong(authentication.getName()));

        return ApiResponse.of(_OK, null);
    }

    @Operation(summary = "주소 삭제하기")
    @DeleteMapping("/setting/addresses")
    public ApiResponse<?> deleteAddress(
            @RequestBody MypageRequestDTO.AddressDto addressDto, Authentication authentication){

        if(authentication == null || !authentication.isAuthenticated())
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);

        mypageService.deleteAddress(addressDto, Long.parseLong(authentication.getName()));

        return ApiResponse.of(_OK, null);
    }
}
