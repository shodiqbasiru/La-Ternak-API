package com.enigma.laternak.dto.request;

import com.enigma.laternak.entity.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Product name is Required")
    private String productName;

    @NotNull(message = "Price is Required")
    @Min(value = 0)
    private Integer price;

    @NotNull(message = "Stock is Required")
    @Min(value = 0)
    private Integer stock;

    @NotBlank(message = "Description is Required")
    private String description;

    @NotBlank(message = "Product name is Required")
    private String storeId;
}
