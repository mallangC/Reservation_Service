package com.zerobase.reservation.controller;

import com.zerobase.reservation.domain.dto.ReservationDto;
import com.zerobase.reservation.domain.form.ReservationForm;
import com.zerobase.reservation.domain.form.ReservationVerifyForm;
import com.zerobase.reservation.model.Reservation;
import com.zerobase.reservation.security.JwtUtil;
import com.zerobase.reservation.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  private final JwtUtil jwtUtil;

  //예약 등록 - 회원
  @PostMapping
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<ReservationDto> addReservation(
          @RequestBody ReservationForm form) {

    return ResponseEntity.ok(reservationService.addReservation(form, LocalDateTime.now()));
  }

  //예약 확인 - 점장
  @GetMapping("/shop")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<List<ReservationDto>> getReservationsForShop(
          @RequestParam String name) {

    return ResponseEntity.ok(reservationService.getAllReservationsForShop(name));
  }

  //예약 확인 - 회원
  @GetMapping("/member")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<List<ReservationDto>> getReservationsForMember(
          @RequestParam Long id) {

    return ResponseEntity.ok(reservationService.getAllReservationsForMember(id));
  }

  //예약 확인 - 회원
  @GetMapping("/member/test")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> getReservationsForMember(
          HttpServletRequest request) {

    String token = request.getHeader("Authorization").substring(7);
    String role = jwtUtil.getRole(token);

    return ResponseEntity.ok(role);
  }

  //예약 취소 - 회원
  @DeleteMapping
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<ReservationDto> deleteReservation(@RequestParam Long id) {

    return ResponseEntity.ok(reservationService.cancelReservation(id));
  }

  //예약 승인 - 점장
  @PutMapping("/shop/approve")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<ReservationDto> updateReservationApproved(
          @RequestBody ReservationVerifyForm form) {

    return ResponseEntity.ok(reservationService.updateReservationApproved(form));
  }

  //도착 확인 (매장 내 키오스크)
  @PutMapping("/shop/arrive")
  public ResponseEntity<ReservationDto> updateReservationArrived(
          @RequestBody ReservationVerifyForm form) {

    return ResponseEntity.ok(reservationService
            .updateReservationArrived(form, LocalDateTime.now()));
  }



}
