package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.ReviewRequest;
import com.enigma.laternak.dto.response.ReviewResponse;
import com.enigma.laternak.entity.Review;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(ReviewRequest request);
    Review getById(String id);
    ReviewResponse getReviewById(String id);
    List<ReviewResponse> getAll();
    void DeleteById(Review review);
}
