package com.zerobase.reservation.service;

import com.zerobase.reservation.domain.dto.ReviewDto;
import com.zerobase.reservation.domain.form.ReviewForm;
import com.zerobase.reservation.domain.form.ReviewUpdateForm;
import com.zerobase.reservation.exception.CustomException;
import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.model.Reservation;
import com.zerobase.reservation.model.Review;
import com.zerobase.reservation.model.Shop;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.repository.ReservationRepository;
import com.zerobase.reservation.repository.ReviewRepository;
import com.zerobase.reservation.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.zerobase.reservation.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;

  private final MemberRepository memberRepository;

  private final ReservationRepository reservationRepository;

  private final ShopRepository shopRepository;


  //리뷰 작성
  @Transactional
  public ReviewDto addReview(ReviewForm form, LocalDateTime now) {

    String formDt = form.getReservedDt();

    //매장이 존재하는지
    Shop shop = shopRepository.findByName(form.getShopName())
            .orElseThrow(()->new CustomException(NOT_FOUND_SHOP));

    //회원이 존재하는지
    Member member = memberRepository.findById(form.getMemberId())
            .orElseThrow(()->new CustomException(NOT_FOUND_MEMBER));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    LocalDateTime reservationDt = LocalDateTime.parse(formDt, formatter);

    //예약한 내역이 있는지
    Reservation reservation =
            reservationRepository.findFirstByMemberAndShopAndReservationDt(
                            member,
                            shop,
                            formDt)
                    .orElseThrow(()-> new CustomException(NOT_FOUND_RESERVATION));

    //예약 승인을 받은 회원인지
    if (!reservation.getIsApproved()){
      throw new CustomException(NOT_APPROVED_RESERVATION);
    }

    //도착확인이 되어있는지
    if (!reservation.getIsArrived()){
      throw new CustomException(NOT_ARRIVED_RESERVATION);
    }

    //예약시간이 지금시간보다 이전인지(매장을 이용하고 리뷰쓰는건지)
    if (reservationDt.isAfter(now)){
      throw new CustomException(WRITE_AFTER_RESERVE_TIME);
    }

    //리뷰를 이미 썼는지
    Optional<Review> reviewOp = reviewRepository.
            findAllByShopAndReservedDt(shop, reservation.getReservationDt());

    if (reviewOp.isPresent()){
      if (!reviewOp.get().getIsExist()){
        //리뷰를 삭제하면 재작성 불가
        throw new CustomException(UNABLE_REWRITE_REVIEW);
      }else {
        //리뷰가 이미 있으므로 재작성 불가
        throw new CustomException(ALREADY_WRITE_REVIEW);
      }
    }


    Review review = reviewRepository.save(Review.builder()
                    .shop(shop)
                    .member(member)
                    .contents(form.getContents())
                    .isExist(true)
                    .reservedDt(formDt)
                    .rating(form.getRating())
                    .build());

    //매장 이름으로 모든 별점을 가지고와서 평균내기
    double avg = reviewRepository.findAllByShop(shop).stream()
            .filter(Review::getIsExist)
            .mapToInt(Review::getRating)
            .average()
            .orElse(0);

    //평균 별점을 소수점 1자리 까지만 shop에 업데이트
    shop.setRating(Math.round(avg * 10) / 10.0);

    return ReviewDto.from(review);
  }

  //리뷰 확인 (점장)
  public List<ReviewDto> getAllReviewsForShop(String shopName) {

    Shop shop = shopRepository.findByName(shopName)
            .orElseThrow(()->new CustomException(NOT_FOUND_SHOP));

    //매장의 삭제(숨김)되지 않은 리뷰를 모두 반환
    return reviewRepository.findAllByShopAndIsExist(shop, true)
            .stream()
            .map(ReviewDto::from)
            .toList();
  }

  //리뷰 확인 (회원)
  public List<ReviewDto> getAllReviewsForMember(Long memberId) {

    Member member = memberRepository.findById(memberId)
            .orElseThrow(()->new CustomException(NOT_FOUND_MEMBER));

    //회원의 삭제(숨김)되지 않은 리뷰를 모두 반환
    return reviewRepository.findAllByMemberAndIsExist(member, true)
            .stream()
            .map(ReviewDto::from)
            .toList();
  }

  //리뷰 수정
  @Transactional
  public ReviewDto updateReview(ReviewUpdateForm form) {

    //아이디로 회원 찾기
    memberRepository.findById(form.getMemberId())
            .orElseThrow(()-> new CustomException(NOT_FOUND_MEMBER));

    //아이디 리뷰 찾기
    Review review = reviewRepository.findById(form.getId())
            .orElseThrow(()-> new CustomException(NOT_FOUND_REVIEW));

    //리뷰 수정
    review.setContents(form.getContents());
    return ReviewDto.from(review);
  }

  //리뷰 삭제
  @Transactional
  public ReviewDto deleteReview(Long id) {
    //아이디로 리뷰 찾기
    Review review = reviewRepository.findById(id)
            .orElseThrow(()-> new CustomException(NOT_FOUND_REVIEW));

    //리뷰가 이미 삭제처리 됐는지 확인
    if (!review.getIsExist()){
      throw new CustomException(ALREADY_DELETE_REVIEW);
    }

    //리뷰 삭제(숨김)처리
    review.setIsExist(false);

    //평균 별점 업데이트
    Shop shop = review.getShop();
    double avg = reviewRepository.findAllByShop(shop).stream()
            .filter(Review::getIsExist)
            .mapToInt(Review::getRating)
            .average()
            .orElse(0);

    shop.setRating(Math.round(avg * 10) / 10.0);

    return ReviewDto.from(review);
  }

}


