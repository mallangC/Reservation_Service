package com.zerobase.reservation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."),
  WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀립니다."),
  ALREADY_REGISTERED_MEMBER(HttpStatus.BAD_REQUEST, "이미 등록된 회원입니다."),
  WRONG_INVALID(HttpStatus.BAD_REQUEST, "잘못된 인증입니다."),
  NULL_TOKEN(HttpStatus.BAD_REQUEST, "토근이 발급되지 않았습니다."),
  TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토근 유효기간이 만료되었습니다."),


  NOT_FOUND_SHOP(HttpStatus.BAD_REQUEST, "매장을 찾을 수 업습니다."),
  ALREADY_REGISTERED_SHOP(HttpStatus.BAD_REQUEST, "이미 등록된 매장입니다."),
  ALREADY_GIVE_STAR(HttpStatus.BAD_REQUEST, "이미 별점을 줬습니다."),
  NOT_GIVE_STAR(HttpStatus.BAD_REQUEST, "별점을 주지 않았습니다."),


  NOT_FOUND_RESERVATION(HttpStatus.BAD_REQUEST, "예약을 찾을 수 없습니다."),
  NOT_APPROVED_RESERVATION(HttpStatus.BAD_REQUEST, "예약이 거절되었습니다."),
  NOT_ARRIVED_RESERVATION(HttpStatus.BAD_REQUEST, "예약시간 내에 도착하지 못한 회원입니다."),
  RESERVATION_WITHIN_TWO_MONTHS(HttpStatus.BAD_REQUEST, "두 달 후까지만 예약할 수 있습니다."),
  NOT_TIME_SHOP_OPEN(HttpStatus.BAD_REQUEST, "매장이 열려있는 시간이 아닙니다."),
  NOT_DAY_SHOP_OPEN(HttpStatus.BAD_REQUEST, "매장이 열려있는 요일이 아닙니다."),
  ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "이미 예약이 되어있습니다."),
  NOT_FOUND_RESERVATION_TO_PHONE(HttpStatus.BAD_REQUEST, "번호로 등록된 예약이 없습니다."),
  RESERVATION_NOT_TODAY(HttpStatus.BAD_REQUEST, "예약 날짜는 오늘이 아닙니다."),
  RESERVATION_EARLY_OR_PASSED(HttpStatus.BAD_REQUEST, "예약 시간 10분 전이 아니거나, 예약시간이 지났습니다."),
  ALREADY_ARRIVED_RESERVATION(HttpStatus.BAD_REQUEST, "이미 도착 확인된 예약입니다."),


  NOT_FOUND_REVIEW(HttpStatus.BAD_REQUEST, "리뷰를 찾을 수 없습니다."),
  ALREADY_WRITE_REVIEW(HttpStatus.BAD_REQUEST, "이미 작성한 리뷰가 있습니다."),
  WRITE_AFTER_RESERVE_TIME(HttpStatus.BAD_REQUEST, "예약시간 이 후에 리뷰를 작성해주세요"),
  ALREADY_DELETE_REVIEW(HttpStatus.BAD_REQUEST, "이미 삭제된 리뷰입니다."),
  UNABLE_REWRITE_REVIEW(HttpStatus.BAD_REQUEST, "리뷰 삭제 후 재작성은 불가능합니다.."),


  DENIED_TOKEN(HttpStatus.BAD_REQUEST, "맞는 권한을 가진 사용자가 아닙니다."),
  EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "토큰 기간이 만료되었습니다."),
  NOT_FOUND_TOKEN(HttpStatus.BAD_REQUEST, "인증되지 않은 사용자 입니다."),
  ;

  private final HttpStatus httpStatus;
  private final String detail;
}
