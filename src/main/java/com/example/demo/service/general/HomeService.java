package com.example.demo.service.general;

import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.Home.HomeResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.RaffleSortType;
import org.springframework.data.domain.Page;

public interface HomeService {

    HomeResponseDTO getHome(int page, int size, RaffleSortType raffleSortType, Boolean includeClosed);

    HomeResponseDTO getHomeLogin(Long userId, int page, int size, RaffleSortType raffleSortType, Boolean raffleNotEnded);

    HomeRaffleListDTO getHomeCategories(String categoryName, int page, int size, RaffleSortType raffleSortType, Boolean raffleNotEnded);

    HomeRaffleListDTO getHomeCategoriesLogin(String categoryName, Long userId, int page, int size, RaffleSortType raffleSortType, Boolean raffleNotEnded);

    HomeRaffleListDTO getHomeApproaching(int page, int size);

    HomeRaffleListDTO getHomeApproachingLogin(Long userId, int page, int size);

    HomeRaffleListDTO getHomeFollowingRaffles(Long userId, int page, int size);

    HomeRaffleListDTO getHomeMoreRaffles(int page, int size, RaffleSortType raffleSortType, Boolean raffleNotEnded);

    HomeRaffleListDTO getHomeMoreRafflesLogin(Long userId, int page, int size,  RaffleSortType raffleSortType, Boolean raffleNotEnded);

    HomeRaffleListDTO getHomeLikeRaffles(Long userId, int page, int size);

}
