package com.enigma.laternak.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "m_store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "store_name", nullable = false, length = 50)
    private String storeName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
