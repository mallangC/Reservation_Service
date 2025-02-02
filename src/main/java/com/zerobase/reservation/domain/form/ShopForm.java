package com.zerobase.reservation.domain.form;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ShopForm {
  private String name;
  private Long ownerId;
  private String address;
  private String addressDetail;
  private Double lat;
  private Double lng;
  private String description;
  private Integer openTime;
  private Integer closeTime;
  private List<String> workingDays;
}
