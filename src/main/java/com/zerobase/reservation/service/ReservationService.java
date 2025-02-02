package com.zerobase.reservation.service;

import com.zerobase.reservation.domain.dto.ReservationDto;
import com.zerobase.reservation.domain.dto.WorkingDaysDto;
import com.zerobase.reservation.domain.form.ReservationForm;
import com.zerobase.reservation.domain.form.ReservationVerifyForm;
import com.zerobase.reservation.exception.CustomException;
import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.model.Reservation;
import com.zerobase.reservation.model.Shop;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.repository.ReservationRepository;
import com.zerobase.reservation.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.zerobase.reservation.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
  private final ReservationRepository reservationRepository;

  private final ShopRepository shopRepository;

  private final MemberRepository memberRepository;

  //예약할 매장 이름,예약하는 회원 아이디, 예약할 시간
  public ReservationDto addReservation(ReservationForm form, LocalDateTime now) {

    String formDt = form.getReservationDt();

    //매장, 회원 존재여부
    Shop shopToReserve = shopRepository.findByName(form.getShopName())
            .orElseThrow(()-> new CustomException(NOT_FOUND_SHOP));
    Member memberReserve = memberRepository.findById(form.getMemberId())
            .orElseThrow(()-> new CustomException(NOT_FOUND_MEMBER));

    //예약날이 두달내에 있는지
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    LocalDateTime reservationDt = LocalDateTime.parse(formDt, formatter);

    if (!reservationDt.isBefore(now.plusMonths(2)) || reservationDt.isBefore(now)){
      throw new CustomException(RESERVATION_WITHIN_TWO_MONTHS);
    }

    //매장 운영시간 확인
    int reserveTime = reservationDt.getHour();

    if (shopToReserve.getOpenTime() > reserveTime || shopToReserve.getCloseTime() <= reserveTime){
      throw new CustomException(NOT_TIME_SHOP_OPEN);
    }

    String StartingWithDt = formDt.split(" ")[0];

    //이미 예약이 있는지 확인
    boolean alreadyReserved =
            reservationRepository.existsByMemberAndShopAndReservationDtStartingWith(
                    memberReserve, shopToReserve, StartingWithDt);

    if (alreadyReserved){
      throw new CustomException(ALREADY_RESERVED_TODAY);
    }

    List<String> shopWorkingDays = WorkingDaysDto.from(shopToReserve.getWorkingDays());
    String reserveDay = reservationDt.getDayOfWeek().toString();

    // 요일 확인
    if (!shopWorkingDays.contains(reserveDay))
      throw new CustomException(NOT_DAY_SHOP_OPEN);

    return ReservationDto.from(
            reservationRepository.save(Reservation.builder()
            .shop(shopToReserve)
            .member(memberReserve)
            .reservationDt(formDt)
            .isArrived(false)
            .build()));
  }

  //매장 이름으로 모든 예약 확인
  public List<ReservationDto> getAllReservationsForShop(String shopName) {

    return reservationRepository.findAllByShopNameOrderByReservationDt(shopName)
            .stream().map(ReservationDto::from).toList();
  }

  //회원 아이디로 모든 예약 확인
  public List<ReservationDto> getAllReservationsForMember(Long memberId) {
    return reservationRepository.findAllByMemberId(memberId)
            .stream().map(ReservationDto::from).toList();
  }

  //예약 취소(삭제)
  @Transactional
  public ReservationDto cancelReservation(Long reservationId) {

    Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(()-> new CustomException(NOT_FOUND_RESERVATION));

    reservationRepository.deleteById(reservationId);

    return ReservationDto.from(reservation);
  }

  //도착 확인
  @Transactional
  public ReservationDto updateReservationArrived(ReservationVerifyForm form, LocalDateTime now) {

    //번호로 찾은 예약이 있는지 확인
    Reservation reservation = validMemberAndReserved(form);

    //예약 승인이 되었는지 확인
    if (reservation.getIsApproved() == null || !reservation.getIsApproved()){
      throw new CustomException(NOT_APPROVED_RESERVATION);
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    LocalDateTime reservationDt = LocalDateTime.parse(reservation.getReservationDt(), formatter);

    //예약 날짜가 맞는지 확인
    if (!now.getMonth().equals(reservationDt.getMonth()) ||
            !now.getDayOfWeek().equals(reservationDt.getDayOfWeek())){
      throw new CustomException(RESERVATION_NOT_TODAY);
    }

    //예약 시간 10분전에 도착했는지 확인
    if (now.isAfter(reservationDt) ||
    now.isBefore(reservationDt.minusMinutes(10))){
      throw new CustomException(RESERVATION_EARLY_OR_PASSED);
    }

    //이미 도착 확인이 되어있는지 확인
    if (reservation.getIsArrived()){
      throw new CustomException(ALREADY_ARRIVED_RESERVATION);
    }

    reservation.setIsArrived(true);
    return ReservationDto.from(reservation);
  }

  //예약 승인
  @Transactional
  public ReservationDto updateReservationApproved(ReservationVerifyForm form) {

    Reservation reservation = validMemberAndReserved(form);

    reservation.setIsApproved(form.getIsApprove().equals("true"));

    return ReservationDto.from(reservation);
  }


  //서비스에 필요한 메서드

  //회원 핸드폰 번호와 매장 이름으로 각 회원, 예약 확인
  private Reservation validMemberAndReserved(ReservationVerifyForm form){
    //회원 확인
    Member member = memberRepository.findByPhone(form.getMemberPhone())
            .orElseThrow(()-> new CustomException(NOT_FOUND_MEMBER));

    //예약 확인
    return reservationRepository.findByMemberIdAndShopName(
            member.getId(), form.getShopName())
            .orElseThrow(()-> new CustomException(NOT_FOUND_RESERVATION_TO_PHONE));
  }


}

