package com.zerobase.reservation.repository;

import com.zerobase.reservation.model.Shop;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

  Optional<Shop> findByName(String name);

  List<Shop> findAllByOrderByNameAsc();

  boolean existsByName(String name);

  List<Shop> findAllByNameContains( String name);

  List<Shop> findAllByNameContainsOrDescriptionContains( String name, String description);
}
