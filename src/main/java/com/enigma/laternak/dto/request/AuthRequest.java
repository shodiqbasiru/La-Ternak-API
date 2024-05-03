package com.enigma.laternak.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email address format")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8,message = "Password must be at least 8 characters long")
    private String password;
}
