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
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "phone number is required")
    @Size(min = 10, max = 13, message = "Phone number must be between 10 and 13 characters long")
    private String phoneNumber;

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6,message = "Password must be at least 6 characters long")
    private String password;
}
