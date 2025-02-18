package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Search.SearchResponseDTO;
import com.example.demo.service.general.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/search")
@RequiredArgsConstructor
public class SearchLoginController {

    private final SearchService searchService;

    @Operation(summary = "최근검색어와 인기검색어 조회")
    @GetMapping("")
    public ApiResponse<SearchResponseDTO.RecentPopularSearchDTO> getRecentPopularSearch(Authentication authentication){
        Long userId = Long.parseLong(authentication.getName());
        SearchResponseDTO.RecentPopularSearchDTO result = searchService.getRecentPopularSearch(userId);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "최근 검색어 삭제")
    @DeleteMapping("")
    public ApiResponse<SearchResponseDTO.DeleteRecentSearchDTO> deleteRecentSearch(@RequestParam("keyword") String keyword, Authentication authentication){
        SearchResponseDTO.DeleteRecentSearchDTO result = searchService.deleteRecentSearch(keyword, Long.parseLong(authentication.getName()));
        return ApiResponse.of(SuccessStatus._OK, result);
    }


    @Operation(summary = "검색")
    @GetMapping("/raffles")
    public ApiResponse<SearchResponseDTO.SearchRaffleListDTO> searchRaffles(@RequestParam("keyword") String keyword, Authentication authentication){
        Long userId = Long.parseLong(authentication.getName());
        SearchResponseDTO.SearchRaffleListDTO result = searchService.searchRaffles(keyword, userId);
        return ApiResponse.of(SuccessStatus._OK, result);
    }
}
