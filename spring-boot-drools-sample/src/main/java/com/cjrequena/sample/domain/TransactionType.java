package com.cjrequena.sample.domain;

import com.cjrequena.sample.exception.service.IllegalArgumentServiceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum TransactionType {
  DEPOSIT("DEPOSIT"),
  WITHDRAWAL("WITHDRAWAL"),
  TRANSFER("TRANSFER");

  private final String type;

  @JsonCreator
  public static TransactionType fromValue(String status) {
    for (TransactionType transactionType : TransactionType.values()) {
      if (transactionType.type.equals(status)) {
        return transactionType;
      }
    }
    throw new IllegalArgumentServiceException("Unexpected status '" + status + "'");
  }

  @JsonValue
  @Override
  public String toString() {
    return String.valueOf(type);
  }

}
