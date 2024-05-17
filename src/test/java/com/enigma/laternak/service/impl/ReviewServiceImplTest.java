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
import com.enigma.laternak.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ProductService productService;
    @Mock
    private UserService userService;
    @Mock
    private ValidationUtil validationUtil;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewRepository, productService, userService, validationUtil);
    }

    @Test
    void shouldReturnReviewWhenCreate() {
        ReviewRequest request = new ReviewRequest();
        request.setRating(5.0);
        request.setComment("Good product");

        Review review = new Review();
        review.setId("1");
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setProduct(new Product());
        review.setUser(new User());

        Mockito.when(userService.getById(request.getUserId())).thenReturn(new User());
        Mockito.when(productService.findById(request.getProductId())).thenReturn(new Product());
        Mockito.when(reviewRepository.saveAndFlush(Mockito.any())).thenReturn(review);

        ReviewResponse result = reviewService.create(request);

        assertEquals(review.getId(), result.getId());
    }

    @Test
    void shouldReturnReviewWhenGetById() {
        Review review = new Review();
        review.setId("1");

        Mockito.when(reviewRepository.findById("1")).thenReturn(Optional.of(review));

        Review result = reviewService.getById("1");

        assertEquals(review, result);
    }

    @Test
    void shouldReturn404WhenGetById() {
        assertThrows(ResponseStatusException.class, () -> {
            reviewService.getById("1");
        });
    }

    @Test
    void shouldReturnReviewListWhenGetAll() {
        Review review = new Review();
        review.setId("1");

        Mockito.when(reviewRepository.findAll()).thenReturn(List.of(review));

        assertEquals(List.of(review), reviewService.getAll());
    }

    @Test
    void shouldDeleteReview() {
        Review review = new Review();
        review.setId("1");

        Mockito.when(reviewRepository.findById("1")).thenReturn(Optional.of(review));

        reviewService.DeleteById(review);

        Mockito.verify(reviewRepository, Mockito.times(1)).delete(review);
    }
}