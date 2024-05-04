package com.enigma.laternak.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {
    @NotBlank(message = "Id is required")
    private String id;
    @NotBlank(message = "Product name is Required")
    private String productName;

    @NotNull(message = "Price is Required")
    @Min(value = 0)
    private Double price;

    @NotNull(message = "Stock is Required")
    @Min(value = 0)
    private Integer stock;

    @NotBlank(message = "Description is Required")
    private String description;
}
