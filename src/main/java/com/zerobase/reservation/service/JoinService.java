package com.zerobase.reservation.service;

import com.zerobase.reservation.domain.form.JoinForm;
import com.zerobase.reservation.exception.CustomException;
import com.zerobase.reservation.exception.ErrorCode;
import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@RequiredArgsConstructor
@Service
public class JoinService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;

  //회원가입
  public void joinProcess(JoinForm form) {
    boolean isExist = memberRepository.existsByEmail(form.getEmail());

    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_REGISTERED_MEMBER);
    }

    Member member = Member.builder()
            .email(form.getEmail().toLowerCase(Locale.ROOT))
            .password(passwordEncoder.encode(form.getPassword()))
            .name(form.getName())
            .phone(form.getPhone())
            .role(form.getRole())
            .build();
    memberRepository.save(member);
  }
}
