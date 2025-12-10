package com.cjrequena.sample.controller.dto.audit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditShadowDTO<T> {
  private T entity;
  private Instant commitDate;
  private String author;
  private String action;
  private String justification;
  private String performedAt;
}
