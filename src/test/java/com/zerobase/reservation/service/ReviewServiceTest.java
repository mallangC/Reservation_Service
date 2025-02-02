package com.zerobase.reservation.service;

import com.zerobase.reservation.domain.dto.ReviewDto;
import com.zerobase.reservation.domain.form.ReviewForm;
import com.zerobase.reservation.domain.form.ReviewUpdateForm;
import com.zerobase.reservation.exception.CustomException;
import com.zerobase.reservation.model.*;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.repository.ReservationRepository;
import com.zerobase.reservation.repository.ReviewRepository;
import com.zerobase.reservation.repository.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.zerobase.reservation.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private ReservationRepository reservationRepository;
  @Mock
  private ShopRepository shopRepository;

  @InjectMocks
  private ReviewService reviewService;

  public String reservationTimeString = "2025-01-25 17";

  public LocalDateTime reservationTime =
          LocalDateTime.of(
                  2025,1,25, 17,0);

  public LocalDateTime reviewBadTime =
          LocalDateTime.of(
                  2025,1,25, 16,0);


  public WorkingDays workingDays = WorkingDays.builder()
          .monday(true)
          .tuesday(true)
          .wednesday(true)
          .thursday(true)
          .friday(true)
          .saturday(true)
          .sunday(true)
          .build();

  public LocalDateTime now = LocalDateTime.now();


  public Shop shop = Shop.builder()
          .id(1L)
          .name("백다방")
          .workingDays(workingDays)
          .openTime(9)
          .closeTime(20)
          .build();

  public Member member = Member.builder()
          .id(1L)
          .email("test@gmail.com")
          .name("Tod")
          .password("1234")
          .phone("01012345678")
          .role("ROLE_MEMBER")
          .build();

  @Test
  @DisplayName("리뷰 작성 성공")
  void addReview() {

    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(shop));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

    given(reservationRepository.findFirstByMemberAndShopAndReservationDt(any(), any(), any()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(shop)
                    .member(member)
                    .reservationDt(reservationTimeString)
                    .isApproved(true)
                    .isArrived(true)
                    .build()));

    given(reviewRepository.save(any()))
            .willReturn(Review.builder()
                    .id(1L)
                    .shop(shop)
                    .member(member)
                    .isExist(true)
                    .rating(5)
                    .reservedDt(reservationTimeString)
                    .contents("넘 많있엉 커피")
                    .build());

    given(reviewRepository.findAllByShopAndReservedDt(any(),any()))
            .willReturn(Optional.empty());

    String reservedDt = String.format("%d-%02d-%02d %d",
            reservationTime.getYear(),
            reservationTime.getMonthValue(),
            reservationTime.getDayOfMonth(),
            reservationTime.getHour());


    ReviewForm form = ReviewForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .contents("넘 많있엉 커피")
            .rating(5)
            .reservedDt(reservedDt)
            .build();

    //when
    ReviewDto result = reviewService.addReview(form, now);

    //then
    assertEquals("넘 많있엉 커피", result.getContents());
  }


  @Test
  @DisplayName("리뷰 작성 실패(없는 매장 이름)")
  void addReviewFailed1() {
    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.empty());

    String reservedDt = String.format("%d-%02d-%02d %d",
            reservationTime.getYear(),
            reservationTime.getMonthValue(),
            reservationTime.getDayOfMonth(),
            reservationTime.getHour());

    ReviewForm form = ReviewForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .contents("넘 많있엉 커피")
            .rating(5)
            .reservedDt(reservedDt)
            .build();

    //when
    try {
      ReviewDto result = reviewService.addReview(form, now);
    }catch (CustomException e) {
      assertEquals(NOT_FOUND_SHOP,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 작성 실패(없는 회원 ID)")
  void addReviewFailed2() {
    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(shop));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

    String reservedDt = String.format("%d-%02d-%02d %d",
            reservationTime.getYear(),
            reservationTime.getMonthValue(),
            reservationTime.getDayOfMonth(),
            reservationTime.getHour());

    ReviewForm form = ReviewForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .contents("넘 많있엉 커피")
            .rating(5)
            .reservedDt(reservedDt)
            .build();

    try {
      ReviewDto result = reviewService.addReview(form, now);
    }catch (CustomException e) {
      assertEquals(NOT_FOUND_MEMBER,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 작성 실패(예약 내역 없음)")
  void addReviewFailed3() {
    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(shop));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

    given(reservationRepository.findFirstByMemberAndShopAndReservationDt(any(), any(), any()))
            .willReturn(Optional.empty());

    String reservedDt = String.format("%d-%02d-%02d %d",
            reservationTime.getYear(),
            reservationTime.getMonthValue(),
            reservationTime.getDayOfMonth(),
            reservationTime.getHour());

    ReviewForm form = ReviewForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .contents("넘 많있엉 커피")
            .rating(5)
            .reservedDt(reservedDt)
            .build();

    try {
      ReviewDto result = reviewService.addReview(form, now);
    }catch (CustomException e) {
      assertEquals(NOT_FOUND_RESERVATION,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 작성 실패(예약 비승인)")
  void addReviewFailed4() {
    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(shop));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

    given(reservationRepository.findFirstByMemberAndShopAndReservationDt(any(), any(), any()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(shop)
                    .member(member)
                    .reservationDt(reservationTimeString)
                    .isApproved(false)
                    .isArrived(true)
                    .build()));

    String reservedDt = String.format("%d-%02d-%02d %d",
            reservationTime.getYear(),
            reservationTime.getMonthValue(),
            reservationTime.getDayOfMonth(),
            reservationTime.getHour());

    ReviewForm form = ReviewForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .contents("넘 많있엉 커피")
            .rating(5)
            .reservedDt(reservedDt)
            .build();

    try {
      ReviewDto result = reviewService.addReview(form, now);
    }catch (CustomException e) {
      assertEquals(NOT_APPROVED_RESERVATION,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 작성 실패(노쇼)")
  void addReviewFailed5() {
    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(shop));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

    given(reservationRepository.findFirstByMemberAndShopAndReservationDt(any(), any(), any()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(shop)
                    .member(member)
                    .reservationDt(reservationTimeString)
                    .isApproved(true)
                    .isArrived(false)
                    .build()));

    String reservedDt = String.format("%d-%02d-%02d %d",
            reservationTime.getYear(),
            reservationTime.getMonthValue(),
            reservationTime.getDayOfMonth(),
            reservationTime.getHour());

    ReviewForm form = ReviewForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .contents("넘 많있엉 커피")
            .rating(5)
            .reservedDt(reservedDt)
            .build();

    try {
      ReviewDto result = reviewService.addReview(form, now);
    }catch (CustomException e) {
      assertEquals(NOT_ARRIVED_RESERVATION,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 작성 실패(예약시간 이전 리뷰작성)")
  void addReviewFailed6() {
    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(shop));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

    given(reservationRepository.findFirstByMemberAndShopAndReservationDt(any(), any(), any()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(shop)
                    .member(member)
                    .reservationDt(reservationTimeString)
                    .isApproved(true)
                    .isArrived(true)
                    .build()));

    String reservedDt = String.format("%d-%02d-%02d %d",
            reservationTime.getYear(),
            reservationTime.getMonthValue(),
            reservationTime.getDayOfMonth(),
            reservationTime.getHour());

    ReviewForm form = ReviewForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .contents("넘 많있엉 커피")
            .rating(5)
            .reservedDt(reservedDt)
            .build();

    try {
      ReviewDto result = reviewService.addReview(form, reviewBadTime);
    }catch (CustomException e) {
      assertEquals(WRITE_AFTER_RESERVE_TIME,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 작성 실패(이미 리뷰를 작성함)")
  void addReviewFailed7() {
    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(shop));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

    given(reservationRepository.findFirstByMemberAndShopAndReservationDt(any(), any(), any()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(shop)
                    .member(member)
                    .reservationDt(reservationTimeString)
                    .isApproved(true)
                    .isArrived(true)
                    .build()));

    given(reviewRepository.findAllByShopAndReservedDt(any(),any()))
            .willReturn(Optional.of(Review.builder()
                    .id(1L)
                    .shop(shop)
                    .member(member)
                    .isExist(true)
                    .rating(5)
                    .reservedDt(reservationTimeString)
                    .contents("넘 많있엉 커피")
                    .build()));

    String reservedDt = String.format("%d-%02d-%02d %d",
            reservationTime.getYear(),
            reservationTime.getMonthValue(),
            reservationTime.getDayOfMonth(),
            reservationTime.getHour());

    ReviewForm form = ReviewForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .contents("넘 많있엉 커피")
            .rating(5)
            .reservedDt(reservedDt)
            .build();

    try {
      ReviewDto result = reviewService.addReview(form, reservationTime);
    }catch (CustomException e) {
      assertEquals(ALREADY_WRITE_REVIEW,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 작성 실패(삭제된 리뷰가 있음(재작성 불가)")
  void addReviewFailed8() {
    //given
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(shop));

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

    given(reservationRepository.findFirstByMemberAndShopAndReservationDt(any(), any(), any()))
            .willReturn(Optional.of(Reservation.builder()
                    .id(1L)
                    .shop(shop)
                    .member(member)
                    .reservationDt(reservationTimeString)
                    .isApproved(true)
                    .isArrived(true)
                    .build()));

    given(reviewRepository.findAllByShopAndReservedDt(any(),any()))
            .willReturn(Optional.of(Review.builder()
                    .id(1L)
                    .shop(shop)
                    .member(member)
                    .isExist(false)
                    .rating(5)
                    .reservedDt(reservationTimeString)
                    .contents("넘 많있엉 커피")
                    .build()));

    String reservedDt = String.format("%d-%02d-%02d %d",
            reservationTime.getYear(),
            reservationTime.getMonthValue(),
            reservationTime.getDayOfMonth(),
            reservationTime.getHour());

    ReviewForm form = ReviewForm.builder()
            .shopName("백다방")
            .memberId(1L)
            .contents("넘 많있엉 커피")
            .rating(5)
            .reservedDt(reservedDt)
            .build();

    try {
      ReviewDto result = reviewService.addReview(form, reservationTime);
    }catch (CustomException e) {
      assertEquals(UNABLE_REWRITE_REVIEW,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 확인 성공(점장)")
  void getReviewsForShop() {
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.of(shop));

    given(reviewRepository.findAllByShopAndIsExist(any(),anyBoolean()))
            .willReturn(List.of(Review.builder()
                                    .id(1L)
                                    .shop(shop)
                                    .member(member)
                                    .contents("good coffee!")
                                    .isExist(true)
                                    .reservedDt("2025-01-10 15")
                                    .rating(5)
                                    .build(),
                            Review.builder()
                                    .id(2L)
                                    .shop(shop)
                                    .member(Member.builder()
                                            .id(4L)
                                            .build())
                                    .contents("good coffee!~!")
                                    .isExist(true)
                                    .reservedDt("2025-01-10 16")
                                    .rating(5)
                                    .build())
                    );

    //when
    List<ReviewDto> result = reviewService.getAllReviewsForShop("백다방");

    //then
    assertEquals(2, result.size());
    assertEquals(4L, result.get(1).getMemberId());
  }


  @Test
  @DisplayName("리뷰 확인 실패(점장)(매장이 없음)")
  void getReviewsForShopFailed() {
    given(shopRepository.findByName(anyString()))
            .willReturn(Optional.empty());


    try {
      List<ReviewDto> result = reviewService.getAllReviewsForShop("백다방");
    }catch (CustomException e) {
      assertEquals(NOT_FOUND_SHOP,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 확인 성공(회원)")
  void getReviewsForMember() {
    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

    given(reviewRepository.findAllByMemberAndIsExist(any(),anyBoolean()))
            .willReturn(List.of(
                    Review.builder()
                            .id(2L)
                            .shop(shop)
                            .member(Member.builder()
                                    .id(4L)
                                    .build())
                            .contents("good ramen!~!")
                            .isExist(true)
                            .reservedDt("2025-01-10 16")
                            .rating(5)
                            .build())
            );

    //when
    List<ReviewDto> result = reviewService.getAllReviewsForMember(4L);

    //then
    assertEquals(1, result.size());
    assertEquals(4L, result.get(0).getMemberId());
  }


  @Test
  @DisplayName("리뷰 확인 실패(회원)(회원이 없음)")
  void getReviewsForMemberFailed() {
    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

    try{
    List<ReviewDto> result = reviewService.getAllReviewsForMember(4L);
    }catch (CustomException e) {
      assertEquals(NOT_FOUND_MEMBER,e.getErrorCode());
    }
  }


  @Test
  @DisplayName("리뷰 수정 성공")
  void updateReview() {

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder()
                    .id(4L)
                    .name("Tod")
                    .email("test@gmail.com")
                    .build()));

    given(reviewRepository.findById(anyLong()))
            .willReturn(Optional.of(Review.builder()
                    .id(1L)
                    .shop(shop)
                    .rating(5)
                    .isExist(true)
                    .reservedDt(reservationTimeString)
                    .contents("JMT")
                    .member(member)
                    .build()));

    ReviewUpdateForm form = ReviewUpdateForm.builder()
            .id(1L)
            .memberId(1L)
            .contents("JMTGURI")
            .build();

    ReviewDto review = reviewService.updateReview(form);

    assertEquals("JMTGURI", review.getContents());
    assertEquals(1L, review.getId());
    assertEquals(1L, review.getMemberId());

  }


  @Test
  @DisplayName("리뷰 수정 실패(회원이 없음)")
  void updateReviewFailed1() {

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());


    ReviewUpdateForm form = ReviewUpdateForm.builder()
            .id(1L)
            .memberId(1L)
            .contents("JMTGURI")
            .build();
  try {
    ReviewDto review = reviewService.updateReview(form);
  }catch (CustomException e) {
    assertEquals(NOT_FOUND_MEMBER, e.getErrorCode());
  }



  }


  @Test
  @DisplayName("리뷰 수정 실패(회원 정보에 맞는 리뷰가 없음)")
  void updateReviewFailed2() {

    given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder()
                    .id(4L)
                    .name("Tod")
                    .email("test@gmail.com")
                    .build()));

    given(reviewRepository.findById(anyLong()))
            .willReturn(Optional.empty());

    ReviewUpdateForm form = ReviewUpdateForm.builder()
            .id(1L)
            .memberId(1L)
            .contents("JMTGURI")
            .build();

    try {
      ReviewDto review = reviewService.updateReview(form);
    }catch (CustomException e) {
      assertEquals(NOT_FOUND_REVIEW, e.getErrorCode());
    }

  }


  @Test
  @DisplayName("리뷰 삭제 성공")
  void deleteReview() {

    given(reviewRepository.findById(anyLong()))
            .willReturn(Optional.of(Review.builder()
                    .id(1L)
                    .shop(shop)
                    .contents("JMT")
                    .member(member)
                    .isExist(true)
                    .build()));

    ReviewDto review = reviewService.deleteReview(1L);

    assertEquals(false, review.getIsExist());
  }


  @Test
  @DisplayName("리뷰 삭제 실패(리뷰가 없음)")
  void deleteReviewFailed1() {

    given(reviewRepository.findById(anyLong()))
            .willReturn(Optional.empty());
    try {
      ReviewDto review = reviewService.deleteReview(1L);
    }catch (CustomException e) {
      assertEquals(NOT_FOUND_REVIEW, e.getErrorCode());
    }

  }

  @Test
  @DisplayName("리뷰 삭제 실패(이미 삭제된 리뷰)")
  void deleteReviewFailed2() {

    given(reviewRepository.findById(anyLong()))
            .willReturn(Optional.of(Review.builder()
                    .id(1L)
                    .shop(shop)
                    .contents("JMT")
                    .member(member)
                    .isExist(false)
                    .build()));

    try {
      ReviewDto review = reviewService.deleteReview(1L);
    }catch (CustomException e) {
      assertEquals(ALREADY_DELETE_REVIEW, e.getErrorCode());
    }
  }


}