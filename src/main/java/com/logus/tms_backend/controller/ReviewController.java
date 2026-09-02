package com.logus.tms_backend.controller;

import com.logus.tms_backend.model.Review;
import com.logus.tms_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /api/reviews — оставить отзыв о перевозчике
     */
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestParam Long orderId,
                                               @RequestParam Long customerId,
                                               @RequestParam Integer rating,
                                               @RequestParam(required = false) String comment) {
        Review review = reviewService.createReview(orderId, customerId, rating, comment);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    /**
     * GET /api/reviews/carrier/{carrierId} — все отзывы о перевозчике
     */
    @GetMapping("/carrier/{carrierId}")
    public ResponseEntity<List<Review>> getReviewsByCarrier(@PathVariable Long carrierId) {
        return ResponseEntity.ok(reviewService.getReviewsByCarrier(carrierId));
    }

    /**
     * GET /api/reviews/customer/{customerId} — все отзывы, оставленные заказчиком
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Review>> getReviewsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(reviewService.getReviewsByCustomer(customerId));
    }

    /**
     * GET /api/reviews/order/{orderId} — отзыв на конкретный заказ
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Review> getReviewByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(reviewService.getReviewByOrderId(orderId));
    }
}