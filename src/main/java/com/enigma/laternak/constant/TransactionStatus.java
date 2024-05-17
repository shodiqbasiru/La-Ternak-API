package com.enigma.laternak.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionStatus {
    // ordered, pending, settlement, cancel, deny, expire, failure
    ORDERED("ordered", "Ordered"),
    PENDING("pending", "Pending"),
    SETTLEMENT("settlement", "Settlement"),
    CANCEL("cancel", "Cancel"),
    DENY("deny", "Deny"),
    EXPIRE("expire", "Expire"),
    FAILURE("failure", "Failure");

    private String name;
    private String description;

    TransactionStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static TransactionStatus getByName(String name) {
        return Arrays.stream(values())
                .filter(transactionStatus -> transactionStatus.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
