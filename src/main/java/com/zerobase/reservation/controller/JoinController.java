package com.zerobase.reservation.controller;

import com.zerobase.reservation.domain.form.JoinForm;
import com.zerobase.reservation.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

  private final JoinService joinService;

  //회원가입
  @PostMapping("/join")
  public ResponseEntity<String> joinProcess(@RequestBody JoinForm joinForm) {
    joinService.joinProcess(joinForm);
    return ResponseEntity.ok("join process completed");
  }

}
