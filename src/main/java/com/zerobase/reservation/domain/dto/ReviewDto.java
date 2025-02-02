package com.zerobase.reservation.domain.dto;

import com.zerobase.reservation.model.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewDto {
  private Long id;
  private String shop;
  private Long memberId;
  private String contents;
  private Boolean isExist;
  private String reservedDt;
  private Integer rating;

  public static ReviewDto from(Review review) {
    return ReviewDto.builder()
            .id(review.getId())
            .shop(review.getShop().getName())
            .memberId(review.getMember().getId())
            .contents(review.getContents())
            .isExist(review.getIsExist())
            .reservedDt(review.getReservedDt())
            .rating(review.getRating())
            .build();
  }
}
