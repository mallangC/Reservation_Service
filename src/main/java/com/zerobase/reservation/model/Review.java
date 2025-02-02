package com.zerobase.reservation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  private Shop shop;
  @ManyToOne
  private Member member;
  private String contents;
  private Boolean isExist;
  private String reservedDt;
  private Integer rating;

}
