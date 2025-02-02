package com.zerobase.reservation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

@AllArgsConstructor
@Builder
@Getter
@Entity
@NoArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Member extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Email
  @Column(unique = true)
  private String email;
  private String password;
  private String name;
  private String phone;
  private String role;

}
