package com.enigma.laternak.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "m_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "customer_name", nullable = false, length = 50)
    private String customerName;

    @Column(name = "number_phone", nullable = false, length = 15)
    private String numberPhone;

    @Column(name = "address")
    private String address;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    @OneToOne
    @JoinColumn(name = "image_id", unique = true)
    private ImageUser imageUser;

    @OneToOne(mappedBy = "user")
    private Store store;
}
