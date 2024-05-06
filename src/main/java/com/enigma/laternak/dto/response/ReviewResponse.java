package com.enigma.laternak.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
        private String id;
        private Double rating;
        private String comment;
        private String userId;
        private String productId;
}
