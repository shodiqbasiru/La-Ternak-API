package com.enigma.laternak.entity;

import com.enigma.laternak.constant.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String token;

    private String redirectUrl;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @OneToOne(mappedBy = "payment")
    private Order order;
}
