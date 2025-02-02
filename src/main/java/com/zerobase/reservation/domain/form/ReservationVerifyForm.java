package com.zerobase.reservation.domain.form;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReservationVerifyForm {
  private String shopName;
  private String memberPhone;
  private String isApprove;
}
