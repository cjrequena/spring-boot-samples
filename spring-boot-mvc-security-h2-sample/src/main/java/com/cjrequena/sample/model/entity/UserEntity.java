package com.cjrequena.sample.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "T_USER")
@Setter
@Getter
public class UserEntity {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String userName;
  private String password;
  private String role; //Eg: ADMIN,USER
}
