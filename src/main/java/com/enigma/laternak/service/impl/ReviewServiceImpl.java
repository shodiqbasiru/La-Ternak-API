package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.ReviewRequest;
import com.enigma.laternak.dto.response.ReviewResponse;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.entity.Review;
import com.enigma.laternak.entity.User;
import com.enigma.laternak.repository.ReviewRepository;
import com.enigma.laternak.service.ProductService;
import com.enigma.laternak.service.ReviewService;
import com.enigma.laternak.service.UserService;
import com.enigma.laternak.util.ResponseMessage;
import com.enigma.laternak.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserService userService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReviewResponse create(ReviewRequest request) {
        validationUtil.validate(request);

        User user = userService.getById(request.getUserId());
        Product product = productService.findById(request.getProductId());
        Review review = reviewRepository.saveAndFlush(Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .product(product)
                .user(user)
                .build());
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .userName(review.getUser().getAccount().getUsername())
                .productId(review.getProduct().getId())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Review getById(String id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, "Review not found"));
    }

    @Override
    public ReviewResponse getReviewById(String id) {
        Review review = getById(id);
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .userName(review.getUser().getAccount().getUsername())
                .productId(review.getProduct().getId())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponse> getAll() {
        return reviewRepository.findAll().stream()
                .map(review -> ReviewResponse.builder()
                        .id(review.getId())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .userName(review.getUser().getAccount().getUsername())
                        .productId(review.getProduct().getId())
                        .build()
                ).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void DeleteById(Review review) {
        Review currentReview = getById(review.getId());
        reviewRepository.delete(currentReview);
    }
}
