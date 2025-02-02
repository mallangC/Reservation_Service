package com.zerobase.reservation.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkingDays {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  private Shop shop;
  private Boolean monday;
  private Boolean tuesday;
  private Boolean wednesday;
  private Boolean thursday;
  private Boolean friday;
  private Boolean saturday;
  private Boolean sunday;

}
