package com.zerobase.reservation.service;

import com.zerobase.reservation.controller.mapper.ShopMapper;
import com.zerobase.reservation.domain.form.ShopForm;
import com.zerobase.reservation.exception.CustomException;
import com.zerobase.reservation.exception.ErrorCode;
import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.model.Shop;
import com.zerobase.reservation.model.WorkingDays;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.repository.ShopRepository;
import com.zerobase.reservation.repository.WorkingDaysRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.zerobase.reservation.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

  @Mock
  private ShopRepository shopRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private WorkingDaysRepository workingDaysRepository;

  @InjectMocks
  private ShopService shopService;

  @Test
  @DisplayName("매장 등록")
  void registerShop() {
    //given
    given(shopRepository.existsByName(anyString()))
            .willReturn(false);

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .password("1234")
                    .phone("01011111111")
                    .name("Tod")
                    .role("ROLE_MANAGER")
                    .build()));

    ArgumentCaptor<Shop> shopCaptor = ArgumentCaptor.forClass(Shop.class);

    ShopForm form = ShopForm.builder()
            .name("김다방")
            .ownerId(1L)
            .workingDays(List.of(
                    "MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"
            ))
            .build();

    shopService.addShop(form);

    verify(shopRepository, times(1)).save(shopCaptor.capture());
  }

  @Test
  @DisplayName("매장 등록 실패(이미 등록된 매장 이름)")
  void registerShopFailed() {
    //given
    given(shopRepository.existsByName(anyString()))
            .willReturn(true);

    ShopForm form = ShopForm.builder()
            .name("김다방")
            .build();

    try {
      shopService.addShop(form);
    }catch (CustomException e){
      assertEquals(ALREADY_REGISTERED_SHOP,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("매장 삭제")
  void deleteShop() {

    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(Shop.builder()
                            .id(1L)
                            .name("백다방")
                            .owner(Member.builder()
                                    .id(1L)
                                    .build())
                            .workingDays(WorkingDays.builder()
                                    .monday(true)
                                    .tuesday(true)
                                    .wednesday(true)
                                    .thursday(true)
                                    .friday(true)
                                    .saturday(true)
                                    .sunday(false)
                                    .build())
                    .build()));


    ArgumentCaptor<Shop> captor = ArgumentCaptor.forClass(Shop.class);

    shopService.deleteShop("백다방");

    verify(shopRepository, times(1)).delete(captor.capture());
  }

  @Test
  @DisplayName("매장 삭제 실패(삭제할 매장이 없음)")
  void deleteShopFailed() {
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.empty());

    try {
      shopService.deleteShop("백다방");
    }catch (CustomException e){
      assertEquals(NOT_FOUND_SHOP, e.getErrorCode());
    }
  }


}