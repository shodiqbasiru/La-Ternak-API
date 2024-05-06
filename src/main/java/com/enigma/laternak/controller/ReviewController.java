package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.ReviewRequest;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.entity.Review;
import com.enigma.laternak.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiRoute.REVIEW_API)
@Tag(name = "Review", description = "Api for review")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
            summary = "Create Review",
            description = "Create a new Review"
    )
    @SecurityRequirement(name = "Authorization")
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<Review>> createReview(@RequestBody ReviewRequest request) {
        Review review = reviewService.create(request);
        CommonResponse<Review> response = CommonResponse.<Review>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Review created successfully")
                .data(review)
                .pagingResponse(null)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get All",
            description = "Get all review"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<List<Review>>> getAllReview() {
        List<Review> reviews = reviewService.getAll();
        CommonResponse<List<Review>> response = CommonResponse.<List<Review>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Review retrieved successfully")
                .data(reviews)
                .pagingResponse(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Delete Review",
            description = "Delete review by id"
    )
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<Review>> deleteReview(@PathVariable String id) {
        Review review = reviewService.getById(id);
        reviewService.DeleteById(review);
        CommonResponse<Review> response = CommonResponse.<Review>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Review deleted successfully")
                .data(review)
                .pagingResponse(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}