package com.zerobase.reservation.service;

import com.zerobase.reservation.controller.mapper.ShopMapper;
import com.zerobase.reservation.domain.dto.ShopDto;
import com.zerobase.reservation.domain.form.MyLocationForm;
import com.zerobase.reservation.domain.form.ShopForm;
import com.zerobase.reservation.exception.CustomException;
import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.model.Shop;
import com.zerobase.reservation.model.WorkingDays;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.repository.ShopRepository;
import com.zerobase.reservation.repository.WorkingDaysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zerobase.reservation.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ShopService {
  private final ShopRepository shopRepository;

  private final ShopMapper shopMapper;

  private final MemberRepository memberRepository;

  private final WorkingDaysRepository workingDaysRepository;


  //모든 매장 이름 확인
  public List<String > getAllShops() {
    List<Shop> shops = shopRepository.findAll();
    List<String> shopNames = new ArrayList<>();
    for (Shop shop : shops) {
      shopNames.add(shop.getName());
    }
    return shopNames;
  }

  //모든 매장 이름 확인 (가,나,다 정렬)
  public List<String> getAllShopsAsc() {
    List<Shop> shops = shopRepository.findAllByOrderByNameAsc();
    List<String> shopNames = new ArrayList<>();
    for (Shop shop : shops) {
      shopNames.add(shop.getName());
    }
    return shopNames;
  }

  //모든 매장 이름 확인(별점 순)
  public List<String> getAllShopsStarCount() {
    List<Shop> shops = shopRepository.findAll();
    Collections.sort(shops);

    List<String> shopNames = new ArrayList<>();
    for (Shop shop : shops) {
      shopNames.add(shop.getName());
    }
    return shopNames;
  }

  //모든 매장 이름 확인(거리 순)
  public List<String> getAllShopsDistance(MyLocationForm form) {

    return shopMapper.selectListDistance(form);
  }

  //매장 이름으로 매장 디테일 확인
  public ShopDto getShop(String shopName){
    return ShopDto.from(findShopByName(shopName));
  }

  //매장 등록
  @Transactional
  public ShopDto addShop(ShopForm form) {
    if (isExist(form.getName())) {
      throw new CustomException(ALREADY_REGISTERED_SHOP);}

    Member member = findMemberById(form.getOwnerId());

    Shop shop = Shop.builder()
            .name(form.getName())
            .owner(member)
            .address(form.getAddress())
            .addressDetail(form.getAddressDetail())
            .lat(form.getLat())
            .lng(form.getLng())
            .description(form.getDescription())
            .openTime(form.getOpenTime())
            .closeTime(form.getCloseTime())
            .build();


    WorkingDays workingDays = getWorkingDays(form, shop);
    shopRepository.save(shop);
    workingDaysRepository.save(workingDays);
    shop.setWorkingDays(workingDays);
    return ShopDto.from(shop);
  }

  //매장 삭제
  @Transactional
  public ShopDto deleteShop(String shopName){

    Shop shop = findShopByName(shopName);
    shopRepository.delete(shop);
    workingDaysRepository.delete(shop.getWorkingDays());
    return ShopDto.from(shop);
  }

  //매장에 별점 확인
  public Double getRating(String shopName){

    Shop shop = findShopByName(shopName);
    return shop.getRating();
  }

  //클래스 내에서 필요한 메서드

  //매장 이름으로 매장이 있는지 확인
  private boolean isExist(String shopName) {
    return shopRepository.existsByName(shopName);
  }

  //매장 이름으로 매장을 찾아서 반환
  private Shop findShopByName(String name) {
    return shopRepository.findByName(name)
            .orElseThrow(()-> new CustomException(NOT_FOUND_SHOP));
  }

  //회원 아이디로 회원을 찾아서 반환
  private Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId)
            .orElseThrow(()->new CustomException(NOT_FOUND_MEMBER));
  }

  //String 형태로 들어온 데이터를 boolean형태의 데이터로 파싱 후 반환
  private WorkingDays getWorkingDays(ShopForm form, Shop shop) {
    WorkingDays workingDays = new WorkingDays();
    //초기화
    workingDays.setMonday(false);
    workingDays.setTuesday(false);
    workingDays.setWednesday(false);
    workingDays.setThursday(false);
    workingDays.setFriday(false);
    workingDays.setSaturday(false);
    workingDays.setSunday(false);

    for (String s : form.getWorkingDays()){
      switch (s){
        case "MONDAY" -> workingDays.setMonday(true);
        case "TUESDAY" -> workingDays.setTuesday(true);
        case "WEDNESDAY" -> workingDays.setWednesday(true);
        case "THURSDAY" -> workingDays.setThursday(true);
        case "FRIDAY" -> workingDays.setFriday(true);
        case "SATURDAY" -> workingDays.setSaturday(true);
        case "SUNDAY" -> workingDays.setSunday(true);
      }
    }
    workingDays.setShop(shop);
    return workingDays;
  }

}
