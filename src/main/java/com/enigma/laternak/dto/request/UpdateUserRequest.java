package com.enigma.laternak.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {
    private String id;

    @NotBlank(message = "name is required")
    @Size(min = 3, max = 50)
    private String customerName;

}
