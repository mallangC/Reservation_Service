package com.zerobase.reservation.domain.form;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReservationForm {
  private String shopName;
  private Long memberId;
  private String reservationDt;
}
