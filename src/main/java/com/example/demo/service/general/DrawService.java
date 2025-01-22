package com.example.demo.service.general;

import com.example.demo.domain.dto.DrawResponseDTO;

public interface DrawService {

    DrawResponseDTO.DrawDto getDrawRaffle(Long raffleId);

    DrawResponseDTO.WinnerDto getWinner(Long raffleId);

    DrawResponseDTO.DeliveryDto getDelivery(Long raffleId);

}
