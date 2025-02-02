package com.zerobase.reservation.service;

import com.zerobase.reservation.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final Member member;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    Collection<GrantedAuthority> authorities = new ArrayList<>();

    authorities.add(new GrantedAuthority() {
      @Override
      public String getAuthority() {
        return member.getRole();
      }
    });

    return authorities;
  }

  @Override
  public String getUsername() {
    return member.getEmail();
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }
}
