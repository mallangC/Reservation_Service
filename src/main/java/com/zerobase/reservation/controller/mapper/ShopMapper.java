package com.zerobase.reservation.controller.mapper;

import com.zerobase.reservation.domain.form.MyLocationForm;
import com.zerobase.reservation.model.Shop;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShopMapper {

//  List<String> selectListDistance(@Param("lat") Double lat, @Param("lng") Double lng);
  List<String> selectListDistance(MyLocationForm form);

  List<Shop> selectAll(Shop parameter);
}
