package com.cjrequena.sample.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Account account;
    
    private double amount;
    private LocalDateTime timestamp;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER
    private String status; // PENDING, COMPLETED, REJECTED
    private String reason; // Reason for rejection if any
}
