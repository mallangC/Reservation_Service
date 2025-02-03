package com.zerobase.reservation.controller;

import com.zerobase.reservation.domain.dto.ShopDto;
import com.zerobase.reservation.domain.form.MyLocationForm;
import com.zerobase.reservation.domain.form.ShopForm;
import com.zerobase.reservation.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/shop")
@RequiredArgsConstructor
public class ShopController {

  private final ShopService shopService;

  //매장 등록 - 점장
  @PostMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<ShopDto> addShop(@RequestBody ShopForm form) {
    return ResponseEntity.ok(shopService.addShop(form));
  }

  //매장 삭제 - 점장
  @DeleteMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<ShopDto> deleteShop(@RequestParam String name) {
    return ResponseEntity.ok(shopService.deleteShop(name));
  }

  //모든 매장 호출
  @GetMapping
  public ResponseEntity<List<String>> getAllShop() {
    return ResponseEntity.ok(shopService.getAllShops());
  }

  //모든 매장 호출(가나다순 정렬)
  @GetMapping("/asc")
  public ResponseEntity<List<String>> getAllShopArrayAsc() {
    return ResponseEntity.ok(shopService.getAllShopsAsc());
  }

  //모든 매장 호출(별점순 정렬)
  @GetMapping("/star")
  public ResponseEntity<List<String>> getAllShopArrayStar() {
    return ResponseEntity.ok(shopService.getAllShopsStarCount());
  }

  //모든 매장 호출(거리순 정렬)
  @GetMapping("/distance")
  public ResponseEntity<List<String>> getAllShopArrayDistance(@RequestBody MyLocationForm form) {
    return ResponseEntity.ok(shopService.getAllShopsDistance(form));
  }

  //매장 디테일 호출
  @GetMapping("/detail")
  public ResponseEntity<ShopDto> getShop(@RequestParam String name) {
    return ResponseEntity.ok(shopService.getShop(name));
  }

  //매장 검색
  @GetMapping("/search")
  public ResponseEntity<List<ShopDto>> getShopContains(@RequestParam String name) {
    return ResponseEntity.ok(shopService.getShopContains(name));
  }


  //별점 호출
  @GetMapping("/detail/rating")
  public ResponseEntity<String> getRating(@RequestParam String name) {
    return ResponseEntity.ok(
            String.format(
                    "%s 매장의 평균 별점은 %.1f점 입니다.", name, shopService.getRating(name) == null? 0 : shopService.getRating(name)));
  }


}
