package com.zerobase.reservation.controller;

import com.zerobase.reservation.domain.dto.ReviewDto;
import com.zerobase.reservation.domain.form.ReviewForm;
import com.zerobase.reservation.domain.form.ReviewUpdateForm;
import com.zerobase.reservation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  //리뷰 작성 - 회원
  @PostMapping
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<ReviewDto> addReview(@RequestBody ReviewForm form) {
    return ResponseEntity.ok(reviewService.addReview(form, LocalDateTime.now()));
  }

  //리뷰 확인 - 점장
  @GetMapping("/shop")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<List<ReviewDto>> getAllReviewsForShop(@RequestParam String name) {
    return ResponseEntity.ok(reviewService.getAllReviewsForShop(name));
  }

  //리뷰 확인 - 회원
  @GetMapping("/member")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<List<ReviewDto>> getAllReviewsForMember(@RequestParam Long id) {
    return ResponseEntity.ok(reviewService.getAllReviewsForMember(id));
  }

  //리뷰 수정 - 회원
  @PutMapping
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<ReviewDto> updateReview(@RequestBody ReviewUpdateForm form) {
    return ResponseEntity.ok(reviewService.updateReview(form));
  }

  //리뷰 삭제
  @DeleteMapping
  public ResponseEntity<ReviewDto> deleteReview(@RequestParam Long id){
    return ResponseEntity.ok(reviewService.deleteReview(id));
  }

}
