package com.zerobase.reservation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  private Shop shop;
  @ManyToOne
  private Member member;
  private LocalDateTime reservationDt;
  private Boolean isApproved;
  private Boolean isArrived;

}
