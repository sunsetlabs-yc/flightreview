package com.aircompany.backofficeapi.controller;

import com.aircompany.backofficeapi.dto.ReviewResponseDto;
import com.aircompany.backofficeapi.dto.ReviewWithFlightDto;
import com.aircompany.backofficeapi.model.Review;
import com.aircompany.backofficeapi.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ReviewWithFlightDto> getReviews(Pageable pageable, 
                                                Authentication authentication,
                                                @RequestParam(required = false) String flightNumber,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String date,
                                                @RequestParam(required = false) String state) {
        String companyName = authentication.getName();
        return service.getReviewsForCompany(companyName, pageable, flightNumber, keyword, date, state);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewWithFlightDto> getReview(@PathVariable UUID id) {
        return service.getReview(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/response")
    public Review respondReview(@PathVariable UUID id,
                                @RequestBody @Valid ReviewResponseDto dto) {
        return service.respondToReview(id, dto.getResponseText(), dto.getNewState());
    }
}
