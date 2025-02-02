package com.zerobase.reservation.domain.dto;

import com.zerobase.reservation.model.WorkingDays;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class WorkingDaysDto {
  private Long id;
  private ShopDto shop;
  private List<String> workingDays;

  public static List<String> from(WorkingDays workingDays) {
    List<String> workingDaysList = new ArrayList<>();

    if (workingDays.getMonday()){
      workingDaysList.add("MONDAY");
    }
    if (workingDays.getTuesday()){
      workingDaysList.add("TUESDAY");
    }
    if (workingDays.getWednesday()){
      workingDaysList.add("WEDNESDAY");
    }
    if (workingDays.getThursday()){
      workingDaysList.add("THURSDAY");
    }
    if (workingDays.getFriday()){
      workingDaysList.add("FRIDAY");
    }
    if (workingDays.getSaturday()){
      workingDaysList.add("SATURDAY");
    }
    if (workingDays.getSunday()){
      workingDaysList.add("SUNDAY");
    }

    return workingDaysList;
  }
}
