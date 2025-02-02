package com.zerobase.reservation.security;

import com.zerobase.reservation.exception.CustomException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static com.zerobase.reservation.exception.ErrorCode.EXPIRED_TOKEN;
import static com.zerobase.reservation.exception.ErrorCode.NOT_FOUND_TOKEN;

@Component
public class CustomAuthenticationEntryPoint  implements AuthenticationEntryPoint {
  private final HandlerExceptionResolver resolver;

  public CustomAuthenticationEntryPoint(
          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void commence(
          HttpServletRequest request,
          HttpServletResponse response,
          AuthenticationException authException) throws IOException, ServletException {

    //인증되지 않은 사용자와 토큰 만료가 될때 여기로 넘어옴
    int status = response.getStatus();
    if (status == 200){
      resolver.resolveException(request, response, null, new CustomException(NOT_FOUND_TOKEN));
    }else {
      resolver.resolveException(request, response, null, new CustomException(EXPIRED_TOKEN));
    }

  }
}
