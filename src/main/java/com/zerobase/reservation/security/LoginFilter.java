package com.zerobase.reservation.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.domain.form.LoginForm;
import com.zerobase.reservation.service.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  private final JwtUtil jwtUtil;

  @Override
  public Authentication attemptAuthentication(
          HttpServletRequest request,
          HttpServletResponse response) throws AuthenticationException
  {
    LoginForm loginForm = new LoginForm();
    try{
      ObjectMapper mapper = new ObjectMapper();
      ServletInputStream inputStream = request.getInputStream();
      String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
      loginForm = mapper.readValue(messageBody, LoginForm.class);
    }catch (IOException e){
      System.out.println("json parsing error"+e.getMessage());
    }

    String email = loginForm.getEmail();
    String password = loginForm.getPassword();

    UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(email, password, null);

    return authenticationManager.authenticate(authToken);
  }

  @Override
  protected void successfulAuthentication(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain chain,
          Authentication authResult) throws IOException, ServletException
  {
    System.out.println("로그인 성공");
    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
    String email = userDetails.getUsername();

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String role = auth.getAuthority();
    String token = jwtUtil.createToken(email, role, 1000 * 60 * 60 * 24 * 7L);

    response.addHeader("Authorization", "Bearer " + token);

  }

  @Override
  protected void unsuccessfulAuthentication(
          HttpServletRequest request,
          HttpServletResponse response,
          AuthenticationException failed) throws IOException, ServletException
  {
    System.out.println("로그인 실패");
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
  }

}
