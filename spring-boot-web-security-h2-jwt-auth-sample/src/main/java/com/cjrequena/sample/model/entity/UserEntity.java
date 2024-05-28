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

  @Column(name = "user_name")
  private String userName;

  @Column(name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "roles")
  private String roles; //Eg: ADMIN,USER

  @Column(name = "authorities")
  private String authorities; // Eg: authority-1,authority-2
}
