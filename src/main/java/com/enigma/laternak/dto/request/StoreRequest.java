package com.enigma.laternak.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreRequest {
    private String id;

    private String storeName;

    private String address;

    @JsonIgnore
    private MultipartFile image;
}
