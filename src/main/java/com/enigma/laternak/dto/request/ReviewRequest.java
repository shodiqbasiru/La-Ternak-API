package com.enigma.laternak.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest {

    @DecimalMin(value = "1.0", message = "Rating must be greater than 1.0")
    @DecimalMax(value = "5.0", message = "Rating must be less than 5.0")
    private Double rating;

    @NotBlank(message = "Comment must be filled")
    private String comment;

    private String userId;
    private String productId;
}
