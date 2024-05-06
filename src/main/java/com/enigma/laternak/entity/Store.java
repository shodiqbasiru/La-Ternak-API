package com.enigma.laternak.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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

    @Column(name = "store_name", nullable = false, length = 50,unique = true)
    private String storeName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "otp", nullable = false, length = 6, unique = true)
    private String otp;

    @Column(name = "otp_generate_time")
    private Date otpGenerateTime;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "store")
    private List<Product> products;
}
