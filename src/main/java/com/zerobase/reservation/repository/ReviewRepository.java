package com.zerobase.reservation.repository;

import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.model.Review;
import com.zerobase.reservation.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  List<Review> findAllByShopAndIsExist(Shop shop, Boolean isExist);

  List<Review> findAllByMemberAndIsExist(Member member, Boolean isExist);

  List<Review> findAllByShop(Shop shop);

  Optional<Review> findAllByShopAndReservedDt(Shop shop, LocalDateTime reservedDt);
}
