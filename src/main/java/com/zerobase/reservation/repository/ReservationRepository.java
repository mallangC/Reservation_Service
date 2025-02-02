package com.zerobase.reservation.repository;

import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.model.Reservation;
import com.zerobase.reservation.model.Shop;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findAllByMemberId(Long id);

  List<Reservation> findAllByShopNameOrderByReservationDt(String shopName);

  Optional<Reservation> findByMemberIdAndShopName(Long memberId, String shopName);

  Optional<Reservation> findFirstByMemberAndShopAndReservationDt(Member member, Shop shop, String reservationDt);

  void deleteById(Long id);

  boolean existsByMemberIdAndShopNameAndReservationDt(Long memberId, @Unique String shopName, String reservationDt);

  boolean existsByMemberAndShopAndReservationDtStartingWith(Member member, Shop shop, String reservationDt);
}
