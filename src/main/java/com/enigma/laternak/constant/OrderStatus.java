package com.enigma.laternak.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderStatus {
    UNPAID("unpaid", "Unpaid"),
    PACKED("packed", "Packed"),
    SEND("send", "Send"),
    RECEIVED("received", "Received");

    private String name;
    private String description;

    OrderStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static OrderStatus getByName(String name) {
        return Arrays.stream(values())
                .filter(transactionStatus -> transactionStatus.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
