package com.zerobase.reservation.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
    log.error("AuthorizationDeniedException is occurred : {}", e.getMessage());

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(e.getMessage());
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<String> expiredJwtExceptionHandler(ExpiredJwtException e) {
    log.error("ExpiredJwtException is occurred : {}", e.getMessage());

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(e.getMessage());
  }


  @ExceptionHandler(CustomException.class)
  public ResponseEntity<String> customExceptionHandler(CustomException e) {
    log.error("CustomException is occurred : {}", e.getMessage());

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new CustomException(e.getErrorCode()).getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> exceptionHandler(Exception e) {
    log.error("Exception is occurred : {}", e.getMessage());

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(e.getMessage());
  }


}
