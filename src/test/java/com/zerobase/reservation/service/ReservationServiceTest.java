package com.zerobase.reservation.service;

import com.zerobase.reservation.domain.dto.ReservationDto;
import com.zerobase.reservation.domain.form.ReservationForm;
import com.zerobase.reservation.domain.form.ReservationVerifyForm;
import com.zerobase.reservation.exception.CustomException;
import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.model.Reservation;
import com.zerobase.reservation.model.Shop;
import com.zerobase.reservation.model.WorkingDays;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.repository.ReservationRepository;
import com.zerobase.reservation.repository.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.zerobase.reservation.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private ShopRepository shopRepository;

  @Mock
  private MemberRepository memberRepository;

  @Spy
  @InjectMocks
  private ReservationService reservationService;

  public LocalDateTime reservationTime =
          LocalDateTime.of(
                  2025,1,25, 17,0);

  public LocalDateTime goodArriveTime = LocalDateTime.of(
          2025,1,25, 16,55);
  public LocalDateTime earlyArriveDay = LocalDateTime.of(
          2025,1,24, 16,55);
  public LocalDateTime earlyArriveTime = LocalDateTime.of(
          2025,1,25, 15,0);

  public LocalDateTime goodReserveTime = LocalDateTime.of(
          2025,1,10, 10,0);

  public WorkingDays workingDays = WorkingDays.builder()
          .monday(true)
          .tuesday(true)
          .wednesday(true)
          .thursday(true)
          .friday(true)
          .saturday(true)
          .sunday(true)
          .build();


  // 도착 인증 테스트
  @Test
  @DisplayName("도착 인증 성공")
  void updateReservationArrived() {

    //given
    given(memberRepository.findByPhone(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    given(reservationRepository.findByMemberIdAndShopName(anyLong(), anyString()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(Shop.builder()
                            .id(1L)
                            .name("백다방")
                            .build())
                    .member(Member.builder()
                            .id(1L)
                            .email("test@gmail.com")
                            .name("Tod")
                            .password("1234")
                            .phone("01012345678")
                            .role("ROLE_MEMBER")
                            .build())
                    .reservationDt(reservationTime)
                    .isApproved(true)
                    .isArrived(false)
                    .build()));

    ReservationVerifyForm form = ReservationVerifyForm.builder()
            .shopName("백다방")
            .memberPhone("1")
            .build();

    //when
    ReservationDto result =
            reservationService.updateReservationArrived(form, goodArriveTime);

    //then
    assertEquals("백다방", result.getShop());
  }


  @Test
  @DisplayName("도착 인증 실패(없는 회원)")
  void updateReservationArrivedFailed1() {

    //given
    given(memberRepository.findByPhone(anyString()))
            .willReturn(Optional.empty());

    ReservationVerifyForm form = ReservationVerifyForm.builder()
            .shopName("백다방")
            .memberPhone("01012345678")
            .build();

    //when
    try{
    ReservationDto result =
            reservationService.updateReservationArrived(form, goodArriveTime);
    }catch (CustomException e){
      assertEquals(NOT_FOUND_MEMBER, e.getErrorCode());
    }

  }


  @Test
  @DisplayName("도착 인증 실패(예약한적 없음)")
  void updateReservationArrivedFiled2() {

    //given
    given(memberRepository.findByPhone(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    given(reservationRepository.findByMemberIdAndShopName(anyLong(), anyString()))
            .willReturn(Optional.empty());

    ReservationVerifyForm form = ReservationVerifyForm.builder()
            .shopName("백다방")
            .memberPhone("1")
            .build();

    try{
      ReservationDto result =
              reservationService.updateReservationArrived(form, goodArriveTime);
    }catch (CustomException e){
      assertEquals(NOT_FOUND_RESERVATION_TO_PHONE, e.getErrorCode());
    }
  }


  @Test
  @DisplayName("도착 인증 실패(점장 비승인)")
  void updateReservationArrivedFiled3() {

    //given
    given(memberRepository.findByPhone(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    given(reservationRepository.findByMemberIdAndShopName(anyLong(), anyString()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(Shop.builder()
                            .id(1L)
                            .name("백다방")
                            .openTime(9)
                            .closeTime(22)
                            .workingDays(workingDays)
                            .build())
                    .member(Member.builder()
                            .id(1L)
                            .email("test@gmail.com")
                            .name("Tod")
                            .password("1234")
                            .phone("01012345678")
                            .role("ROLE_MEMBER")
                            .build())
                    .reservationDt(reservationTime)
                    .isApproved(false)
                    .isArrived(false)
                    .build()));


    ReservationVerifyForm form = ReservationVerifyForm.builder()
            .shopName("백다방")
            .memberPhone("1")
            .build();

    try{
      ReservationDto result =
              reservationService.updateReservationArrived(form, goodArriveTime);
    }catch (CustomException e){
      assertEquals(NOT_APPROVED_RESERVATION, e.getErrorCode());
    }
  }


  @Test
  @DisplayName("도착 인증 실패(예약 날짜 아님)")
  void updateReservationArrivedFiled4() {


    //given
    given(memberRepository.findByPhone(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    given(reservationRepository.findByMemberIdAndShopName(anyLong(), anyString()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(Shop.builder()
                            .id(1L)
                            .name("백다방")
                            .openTime(9)
                            .closeTime(22)
                            .workingDays(workingDays)
                            .build())
                    .member(Member.builder()
                            .id(1L)
                            .email("test@gmail.com")
                            .name("Tod")
                            .password("1234")
                            .phone("01012345678")
                            .role("ROLE_MEMBER")
                            .build())
                    .reservationDt(reservationTime)
                    .isApproved(true)
                    .isArrived(false)
                    .build()));

    ReservationVerifyForm form = ReservationVerifyForm.builder()
            .shopName("백다방")
            .memberPhone("1")
            .build();

    try{
      ReservationDto result =
              reservationService.updateReservationArrived(form, earlyArriveDay);
    }catch (CustomException e){
      assertEquals(RESERVATION_NOT_TODAY, e.getErrorCode());
    }
  }


  @Test
  @DisplayName("도착 인증 실패(너무 이르거나 늦음)")
  void updateReservationArrivedFiled5() {

    //given
    given(memberRepository.findByPhone(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    given(reservationRepository.findByMemberIdAndShopName(anyLong(), anyString()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(Shop.builder()
                            .id(1L)
                            .name("백다방")
                            .openTime(9)
                            .closeTime(22)
                            .workingDays(workingDays)
                            .build())
                    .member(Member.builder()
                            .id(1L)
                            .email("test@gmail.com")
                            .name("Tod")
                            .password("1234")
                            .phone("01012345678")
                            .role("ROLE_MEMBER")
                            .build())
                    .reservationDt(reservationTime)
                    .isApproved(true)
                    .isArrived(false)
                    .build()));

    ReservationVerifyForm form = ReservationVerifyForm.builder()
            .shopName("백다방")
            .memberPhone("1")
            .build();

    try{
      ReservationDto result =
              reservationService.updateReservationArrived(form, earlyArriveTime);
    }catch (CustomException e){
      assertEquals(RESERVATION_EARLY_OR_PASSED, e.getErrorCode());
    }
  }


  @Test
  @DisplayName("도착 인증 실패(이미 도착 확인)")
  void updateReservationArrivedFiled6() {

    //given
    given(memberRepository.findByPhone(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    given(reservationRepository.findByMemberIdAndShopName(anyLong(), anyString()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(Shop.builder()
                            .id(1L)
                            .name("백다방")
                            .openTime(9)
                            .closeTime(22)
                            .workingDays(workingDays)
                            .build())
                    .member(Member.builder()
                            .id(1L)
                            .email("test@gmail.com")
                            .name("Tod")
                            .password("1234")
                            .phone("01012345678")
                            .role("ROLE_MEMBER")
                            .build())
                    .reservationDt(reservationTime)
                    .isApproved(true)
                    .isArrived(true)
                    .build()));

    ReservationVerifyForm form = ReservationVerifyForm.builder()
            .shopName("백다방")
            .memberPhone("1")
            .build();

    try{
      ReservationDto result =
              reservationService.updateReservationArrived(form, goodArriveTime);
    }catch (CustomException e){
      assertEquals(ALREADY_ARRIVED_RESERVATION, e.getErrorCode());
    }
  }


 // 예약 추가 테스트
  @Test
  @DisplayName("예약 신청 성공")
  void addReservation() {

    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(Shop.builder()
                    .id(1L)
                    .name("백다방")
                    .openTime(9)
                    .closeTime(20)
                    .workingDays(workingDays)
                    .build()));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    given(reservationRepository.save(any()))
            .willReturn(Reservation.builder()
                    .id(1L)
                    .shop(Shop.builder()
                            .id(1L)
                            .name("백다방")
                            .openTime(9)
                            .closeTime(20)
                            .workingDays(workingDays)
                            .build())
                    .member(Member.builder()
                            .id(1L)
                            .email("test@gmail.com")
                            .name("Tod")
                            .password("1234")
                            .phone("01012345678")
                            .role("ROLE_MEMBER")
                            .build())
                    .isArrived(false)
                    .reservationDt(goodReserveTime)
                    .build());

    given(reservationRepository.existsByMemberIdAndShopName(anyLong(), anyString()))
            .willReturn(false);

    //when
    ReservationForm form = ReservationForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .reservationDt("2025-01-25 15")
            .build();

    ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);

    ReservationDto reservationDto = reservationService.addReservation(form, goodReserveTime);

    verify(reservationRepository,times(1)).save(reservationCaptor.capture());
  }

  @Test
  @DisplayName("예약 신청 실패(매장이 존재하지 않음)")
  void addReservationFiled1() {

    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.empty());

    //when
    ReservationForm form = ReservationForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .reservationDt("2025-01-01 15")
            .build();

    try {
      ReservationDto reservationDto = reservationService.addReservation(form, goodReserveTime);
    }catch (CustomException e){
      assertEquals(NOT_FOUND_SHOP, e.getErrorCode());
    }
  }

  @Test
  @DisplayName("예약 신청 실패(회원이 존재하지 않음)")
  void addReservationFiled2() {

    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(Shop.builder()
                    .id(1L)
                    .name("백다방")
                    .openTime(9)
                    .closeTime(20)
                    .workingDays(workingDays)
                    .build()));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());
    //when
    ReservationForm form = ReservationForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .reservationDt("2025-01-01 15")
            .build();

    try {
      ReservationDto reservationDto = reservationService.addReservation(form, goodReserveTime);
    }catch (CustomException e){
      assertEquals(NOT_FOUND_MEMBER, e.getErrorCode());
    }
  }

  @Test
  @DisplayName("예약 신청 실패(맞지 않은 날짜(과거 or 두 달 후)")
  void addReservationFiled3() {

    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(Shop.builder()
                    .id(1L)
                    .name("백다방")
                    .openTime(9)
                    .closeTime(20)
                    .workingDays(workingDays)
                    .build()));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    ReservationForm form = ReservationForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .reservationDt("2025-03-30 15")
            .build();

    try {
      ReservationDto reservationDto = reservationService.addReservation(form, goodReserveTime);
    }catch (CustomException e){
      assertEquals(RESERVATION_WITHIN_TWO_MONTHS, e.getErrorCode());
    }
  }

  @Test
  @DisplayName("예약 신청 실패(매장이 열린 시간이 아님(open, close)")
  void addReservationFiled4() {

    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(Shop.builder()
                    .id(1L)
                    .name("백다방")
                    .openTime(9)
                    .closeTime(20)
                    .workingDays(workingDays)
                    .build()));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    ReservationForm form = ReservationForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .reservationDt("2025-01-30 22")
            .build();

    try {
      ReservationDto reservationDto = reservationService.addReservation(form, goodReserveTime);
    }catch (CustomException e){
      assertEquals(NOT_TIME_SHOP_OPEN, e.getErrorCode());
    }
  }

  @Test
  @DisplayName("예약 신청 실패(이미 다른 예약이 존재함)")
  void addReservationFiled5() {

    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(Shop.builder()
                    .id(1L)
                    .name("백다방")
                    .openTime(9)
                    .closeTime(20)
                    .workingDays(workingDays)
                    .build()));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    given(reservationRepository.existsByMemberIdAndShopName(anyLong(), anyString()))
            .willReturn(true);

    //when
    ReservationForm form = ReservationForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .reservationDt("2025-01-30 15")
            .build();

    try {
      ReservationDto reservationDto = reservationService.addReservation(form, goodReserveTime);
    }catch (CustomException e){
      assertEquals(ALREADY_RESERVED, e.getErrorCode());
    }
  }

  @Test
  @DisplayName("예약 신청 실패(열지 않는 요일에 예약 신청)")
  void addReservationFailed6() {

    WorkingDays workingDaysFail = WorkingDays.builder()
            .id(1L)
            .shop(Shop.builder()
                    .id(1L)
                    .name("백다방")
                    .openTime(9)
                    .closeTime(20)
                    .build())
                  .monday(true)
                  .tuesday(true)
                  .wednesday(true)
                  .thursday(true)
                  .friday(true)
                  .saturday(true)
                  .sunday(false)
                  .build();

    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(Shop.builder()
                    .id(1L)
                    .name("백다방")
                    .openTime(9)
                    .closeTime(20)
                    .workingDays(workingDaysFail)
                    .build()));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("Tod")
                    .password("1234")
                    .phone("01012345678")
                    .role("ROLE_MEMBER")
                    .build()));

    given(reservationRepository.existsByMemberIdAndShopName(anyLong(), anyString()))
            .willReturn(false);

    ReservationForm form = ReservationForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .reservationDt("2025-02-02 15") //일요일
            .build();

    try {
      ReservationDto reservationDto = reservationService.addReservation(form, goodReserveTime);
    }catch (CustomException e){
      assertEquals(NOT_DAY_SHOP_OPEN, e.getErrorCode());
    }
  }





}