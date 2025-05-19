package com.cjrequena.sample.domain;

import com.cjrequena.sample.exception.service.IllegalArgumentServiceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum TransactionStatus {

    COMPLETED("COMPLETED"),
    REJECTED("REJECTED");

    private final String status;

    @JsonCreator
    public static TransactionStatus fromValue(String status) {
        for (TransactionStatus transactionStatus : TransactionStatus.values()) {
            if (transactionStatus.status.equals(status)) {
                return transactionStatus;
            }
        }
        throw new IllegalArgumentServiceException("Unexpected status '" + status + "'");
    }

    @JsonValue
    @Override
    public String toString() {
        return String.valueOf(status);
    }

}
