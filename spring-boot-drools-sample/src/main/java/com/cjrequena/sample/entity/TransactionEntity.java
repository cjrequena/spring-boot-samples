package com.cjrequena.sample.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class TransactionEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "account_id")
  private Long accountId;

  private double amount;
  private LocalDateTime timestamp;
  private String type;   // DEPOSIT, WITHDRAWAL, TRANSFER
  private String status; // PENDING, COMPLETED, REJECTED
  private String reason; // Reason for rejection if any
}
