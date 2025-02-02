package com.zerobase.reservation.domain.dto;

import com.zerobase.reservation.model.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationDto {
  private Long id;
  private String shop;
  private Long memberId;
  private LocalDateTime reservationDt;
  private Boolean isApproved;
  private Boolean isArrived;

  public static ReservationDto from(Reservation reservation) {
    return ReservationDto.builder()
            .id(reservation.getId())
            .shop(reservation.getShop().getName())
            .memberId(reservation.getMember().getId())
            .reservationDt(reservation.getReservationDt())
            .isApproved(reservation.getIsApproved())
            .isArrived(reservation.getIsArrived())
            .build();
  }
}
