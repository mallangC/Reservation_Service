package com.zerobase.reservation.repository;

import com.zerobase.reservation.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmail(String email);

  boolean existsByEmail(String email);

  Optional<Member> findByPhone(String phone);
}
