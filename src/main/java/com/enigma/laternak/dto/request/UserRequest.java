package com.enigma.laternak.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String id;

    private String customerName;

    private String address;

    @JsonIgnore
    private MultipartFile image;
}
