package com.zerobase.reservation.domain.dto;

import com.zerobase.reservation.model.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
  private Long id;
  private String email;
  private String password;
  private String name;
  private String phone;
  private String role;

  public static MemberDto from(Member member) {
    return MemberDto.builder()
            .id(member.getId())
            .email(member.getEmail())
            .name(member.getName())
            .phone(member.getPhone())
            .role(member.getRole())
            .build();
  }

}
