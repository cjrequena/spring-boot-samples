package com.cjrequena.sample.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC) // Ensure JPA can access it
@AllArgsConstructor
@Entity
@Table(name = "account")
public class AccountEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "account_id")
  private Long accountId;
  private String accountNumber;
  private String customerName;
  private double balance;
  private String accountType; // SAVINGS, CHECKING, etc.
  private boolean premium;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TransactionEntity> transactions;
}
