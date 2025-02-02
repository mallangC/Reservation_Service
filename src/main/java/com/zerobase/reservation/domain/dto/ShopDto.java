package com.zerobase.reservation.domain.dto;

import com.zerobase.reservation.model.Shop;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ShopDto {
  private Long id;
  private Long ownerId;
  private String name;
  private String address;
  private String addressDetail;
  private Double lat;
  private Double lng;
  private String description;
  private Double rating;

  private Integer openTime;
  private Integer closeTime;
  private List<String> workingDays;

  public static ShopDto from(Shop shop) {
    List<String> workingDays = new ArrayList<>();
    if (shop.getWorkingDays().getMonday()){
      workingDays.add("MONDAY");
    }
    if (shop.getWorkingDays().getTuesday()){
      workingDays.add("TUESDAY");
    }
    if (shop.getWorkingDays().getWednesday()){
      workingDays.add("WEDNESDAY");
    }
    if (shop.getWorkingDays().getThursday()){
      workingDays.add("THURSDAY");
    }
    if (shop.getWorkingDays().getFriday()){
      workingDays.add("FRIDAY");
    }
    if (shop.getWorkingDays().getSaturday()){
      workingDays.add("SATURDAY");
    }
    if (shop.getWorkingDays().getSunday()){
      workingDays.add("SUNDAY");
    }

    return ShopDto.builder()
            .id(shop.getId())
            .ownerId(shop.getOwner().getId())
            .name(shop.getName())
            .address(shop.getAddress())
            .addressDetail(shop.getAddressDetail())
            .lat(shop.getLat())
            .lng(shop.getLng())
            .description(shop.getDescription())
            .rating(shop.getRating())
            .openTime(shop.getOpenTime())
            .closeTime(shop.getCloseTime())
            .workingDays(workingDays)
            .build();
  }
}
