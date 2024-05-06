package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.ReviewRequest;
import com.enigma.laternak.entity.Review;

import java.util.List;

public interface ReviewService {
    Review create(ReviewRequest request);
    Review getById(String id);
    List<Review> getAll();
    void DeleteById(Review review);
}
