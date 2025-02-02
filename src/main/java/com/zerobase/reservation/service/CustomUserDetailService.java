package com.zerobase.reservation.service;

import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Member member = memberRepository.findByEmail(email).orElse(null);
    
    if (member != null) {
      // 여기서 회원을 검증
      return new CustomUserDetails(member);
    }
    return null;
  }
}
