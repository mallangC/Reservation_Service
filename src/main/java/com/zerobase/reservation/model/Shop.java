package com.zerobase.reservation.model;

import com.zerobase.reservation.domain.form.ShopForm;
import com.zerobase.reservation.repository.ShopRepository;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop implements Comparable<Shop> {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  private Member owner;
  @Unique
  private String name;
  private String address;
  private String addressDetail;
  private Double lat;
  private Double lng;
  private String description;
  private Double rating;
  private Integer openTime;
  private Integer closeTime;
  @ManyToOne
  private WorkingDays workingDays;

  @Override
  public int compareTo(Shop o) {
    return (int) (o.getRating() - this.rating);
  }
}
