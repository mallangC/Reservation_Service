package com.zerobase.reservation.model;

import com.zerobase.reservation.domain.form.ReviewForm;
import com.zerobase.reservation.repository.ShopRepository;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
  private LocalDateTime reservedDt;
  private Integer rating;

}
