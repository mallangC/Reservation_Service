package com.zerobase.reservation.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.exception.ErrorCode;
import com.zerobase.reservation.model.Member;
import com.zerobase.reservation.service.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      System.out.println("token null");
      filterChain.doFilter(request, response);
      return;
    }
    System.out.println("authorization now");
    String token = authHeader.split(" ")[1];

    if (jwtUtil.isExpired(token)) {
      System.out.println("token expired");
      filterChain.doFilter(request, response);
      return;
    }

    String email = jwtUtil.getUsername(token);
    String role = jwtUtil.getRole(token);

    Member member = Member.builder()
            .email(email)
            .password("temppassword")
            .role(role)
            .build();

    CustomUserDetails customUserDetails = new CustomUserDetails(member);

    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);
    filterChain.doFilter(request, response);
  }

  public static void setErrorResponse (
          HttpServletResponse response,
          ErrorCode code) throws IOException
  {
    ObjectMapper mapper = new ObjectMapper();
    response.setStatus(code.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ErrorResponse errorResponse = new ErrorResponse(code.getHttpStatus(),code.getDetail());

    response.getWriter().write(mapper.writeValueAsString(errorResponse));
  }

  @Data
  public static class ErrorResponse {
    private final HttpStatus httpStatus;
    private final String message;
  }

}
