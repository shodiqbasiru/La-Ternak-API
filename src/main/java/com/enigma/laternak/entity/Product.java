package com.enigma.laternak.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "m_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "product_name", nullable = false, length = 50)
    private String productName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @JsonBackReference
    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @JsonBackReference
    @OneToMany(mappedBy = "product")
    private List<ImageProduct> images;
}
