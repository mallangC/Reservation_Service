package com.zerobase.reservation.domain.form;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReviewForm {
  private String shopName;
  private Long memberId;
  private String contents;
  private String reservedDt;
  private Integer rating;
}
