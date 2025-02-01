package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.repository.*;
import com.example.demo.service.general.DeliverySchedulerService;
import com.example.demo.service.general.DeliveryService;
import com.example.demo.service.general.DrawService;
import com.example.demo.service.general.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.domain.converter.DeliveryConverter.*;
import static com.example.demo.domain.converter.MypageConverter.toAddressDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;
    private final DrawService drawService;
    private final DeliverySchedulerService deliverySchedulerService;
    private final EmailService emailService;

    @Override
    public DeliveryResponseDTO.DeliveryDto getDelivery(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        if (deliveryStatus == DeliveryStatus.WAITING_ADDRESS
                || deliveryStatus == DeliveryStatus.WAITING_PAYMENT)
            return toDeliveryDto(delivery, null);

        MypageResponseDTO.AddressDto addressDto = toAddressDto(delivery.getAddress());
        return toDeliveryDto(delivery, addressDto);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto setAddress(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        switch (deliveryStatus){
            case CANCELLED:
                throw new CustomException(ErrorStatus.DELIVERY_CANCELLED);
            case ADDRESS_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
        }

        if (deliveryStatus != DeliveryStatus.WAITING_ADDRESS
                && deliveryStatus != DeliveryStatus.WAITING_PAYMENT)
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_READY);

        List<Address> addressList = user.getAddresses();
        if (addressList.isEmpty())
            throw new CustomException(ErrorStatus.ADDRESS_EMPTY);

        Address defaultAddress = addressList.stream()
                .filter(Address::isDefault)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NO_DEFAULT_ADDRESS));

        delivery.setAddress(defaultAddress);
        delivery.setDeliveryStatus(DeliveryStatus.WAITING_PAYMENT);
        deliveryRepository.save(delivery);

        return DeliveryResponseDTO.ResponseDto.builder()
                .deliveryId(deliveryId)
                .build();
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto complete(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        switch (deliveryStatus){
            case CANCELLED:
                throw new CustomException(ErrorStatus.DELIVERY_CANCELLED);
            case ADDRESS_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
            case WAITING_ADDRESS:
                throw new CustomException(ErrorStatus.DELIVERY_BEFORE_ADDRESS);
        }

        if (deliveryStatus != DeliveryStatus.WAITING_PAYMENT)
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_READY);

        deliverySchedulerService.cancelDeliveryJob(delivery);

        delivery.setDeliveryStatus(DeliveryStatus.READY);
        delivery.setShippingDeadline();
        deliveryRepository.save(delivery);

        emailService.sendOwnerReadyEmail(delivery);

        return DeliveryResponseDTO.ResponseDto.builder()
                .deliveryId(deliveryId)
                .build();
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.WaitDto waitShipping(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        switch (deliveryStatus) {
            case WAITING_ADDRESS:
            case WAITING_PAYMENT:
                throw new CustomException(ErrorStatus.DELIVERY_BEFORE_ADDRESS);
            case ADDRESS_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
            case READY:
                throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_NOT_EXPIRED);
            case SHIPPED:
                throw new CustomException(ErrorStatus.DELIVERY_ALREADY_SHIPPED);
            case CANCELLED:
                throw new CustomException(ErrorStatus.DELIVERY_CANCELLED);
        }

        if (delivery.isShippingExtended())
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_EXTEND);

        deliverySchedulerService.cancelDeliveryJob(delivery);
      
        delivery.extendShippingDeadline();
        deliveryRepository.save(delivery);

        deliverySchedulerService.scheduleDeliveryJob(delivery);

        return toWaitDto(delivery);
    }

    @Override
    @Transactional
    public String cancel(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateWinner(delivery, user);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        switch (deliveryStatus) {
            case WAITING_ADDRESS:
            case WAITING_PAYMENT:
                throw new CustomException(ErrorStatus.DELIVERY_BEFORE_ADDRESS);
            case ADDRESS_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
            case READY:
                throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_NOT_EXPIRED);
            case SHIPPED:
                throw new CustomException(ErrorStatus.DELIVERY_ALREADY_SHIPPED);
            case CANCELLED:
                throw new CustomException(ErrorStatus.DELIVERY_CANCELLED);
        }

        Raffle raffle = delivery.getRaffle();
        drawService.cancel(raffle);

        delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);
        deliveryRepository.save(delivery);

        deliverySchedulerService.cancelDeliveryJob(delivery);
        emailService.sendOwnerCancelEmail(raffle);

        return String.format(Constants.DELIVERY_WINNER_URL, delivery.getId());
    }

    @Override
    public DeliveryResponseDTO.ResultDto getResult(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateOwner(delivery, user);

        Raffle raffle = delivery.getRaffle();
        int applyNum = applyRepository.countByRaffle(raffle);
        int ticket = raffle.getTicketNum();

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        if (deliveryStatus != DeliveryStatus.READY
                && deliveryStatus != DeliveryStatus.SHIPPED)
            return toResultDto(delivery, applyNum * ticket, null);

        MypageResponseDTO.AddressDto addressDto = toAddressDto(delivery.getAddress());
        return toResultDto(delivery, applyNum * ticket, addressDto);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto addInvoice(
            Long deliveryId, DeliveryRequestDTO deliveryRequestDTO) {

        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateOwner(delivery, user);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();
        switch (deliveryStatus) {
            case WAITING_ADDRESS:
            case WAITING_PAYMENT:
                throw new CustomException(ErrorStatus.DELIVERY_BEFORE_ADDRESS);
            case ADDRESS_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_EXPIRED);
            case SHIPPED:
                throw new CustomException(ErrorStatus.DELIVERY_ALREADY_SHIPPED);
            case SHIPPING_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_EXPIRED);
            case CANCELLED:
                throw new CustomException(ErrorStatus.DELIVERY_CANCELLED);
        }

        deliverySchedulerService.cancelDeliveryJob(delivery);

        delivery.setInvoiceNumber(deliveryRequestDTO.getInvoiceNumber());
        delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);
        deliveryRepository.save(delivery);

        // Todo: 자동 수령 완료 설정
//        deliverySchedulerService.scheduleDeliveryJob(delivery);

        return DeliveryResponseDTO.ResponseDto.builder()
                .deliveryId(deliveryId)
                .build();
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.WaitDto waitAddress(Long deliveryId) {
        User user = getUser();
        Delivery delivery = getDeliveryById(deliveryId);
        validateOwner(delivery, user);

        DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();

        switch (deliveryStatus) {
            case WAITING_ADDRESS:
            case WAITING_PAYMENT:
                throw new CustomException(ErrorStatus.DELIVERY_ADDRESS_NOT_EXPIRED);
            case READY:
                throw new CustomException(ErrorStatus.DELIVERY_ALREADY_READY);
            case SHIPPED:
                throw new CustomException(ErrorStatus.DELIVERY_ALREADY_SHIPPED);
            case SHIPPING_EXPIRED:
                throw new CustomException(ErrorStatus.DELIVERY_SHIPPING_EXPIRED);
            case CANCELLED:
                throw new CustomException(ErrorStatus.DELIVERY_CANCELLED);
        }

        if (delivery.isAddressExtended())
            throw new CustomException(ErrorStatus.DELIVERY_ALREADY_EXTEND);

        deliverySchedulerService.cancelDeliveryJob(delivery);

        delivery.extendAddressDeadline();
        deliveryRepository.save(delivery);

        deliverySchedulerService.scheduleDeliveryJob(delivery);

        return toWaitDto(delivery);
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        return userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }

    private Delivery getDeliveryById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DELIVERY_NOT_FOUND));
    }

    private void validateWinner(Delivery delivery, User user) {
        if (!user.equals(delivery.getWinner()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_WINNER);
    }

    private void validateOwner(Delivery delivery, User user) {
        if (!user.equals(delivery.getUser()))
            throw new CustomException(ErrorStatus.DELIVERY_NOT_OWNER);
    }
}
