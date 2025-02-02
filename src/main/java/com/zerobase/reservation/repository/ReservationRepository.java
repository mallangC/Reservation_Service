package com.zerobase.reservation.repository;

import com.zerobase.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findAllByMemberId(Long id);

  List<Reservation> findAllByShopNameOrderByReservationDt(String shopName);

  Optional<Reservation> findByMemberIdAndShopName(Long memberId, String shopName);

  boolean existsByMemberIdAndShopName(Long memberId, String shopName);

  void deleteById(Long id);
}
