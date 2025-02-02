package com.zerobase.reservation.domain.form;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReviewUpdateForm {
  private Long id;
  private Long memberId;
  private String contents;
}
