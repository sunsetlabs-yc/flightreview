package com.aircompany.backofficeapi.service;

import com.aircompany.backofficeapi.dto.ReviewWithFlightDto;
import com.aircompany.backofficeapi.model.Flight;
import com.aircompany.backofficeapi.model.Review;
import com.aircompany.backofficeapi.model.ReviewState;
import com.aircompany.backofficeapi.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository repository;
    private final FlightService flightService;

    public ReviewService(ReviewRepository repository, FlightService flightService) {
        this.repository = repository;
        this.flightService = flightService;
    }

    public Page<ReviewWithFlightDto> getReviewsForCompany(String companyName, Pageable pageable) {
        Page<Review> reviews = repository.findByCompanyName(companyName, pageable);
        return reviews.map(review -> {
            Flight flight = review.getFlightNumber() != null ? flightService.getFlightByFlightNumber(review.getFlightNumber()) : null;
            return new ReviewWithFlightDto(review, flight);
        });
    }

    public Page<ReviewWithFlightDto> getReviewsForCompany(String companyName, Pageable pageable, 
                                                          String flightNumber, String keyword, 
                                                          String date, String state) {
        LocalDate dateFilter = null;
        if (date != null && !date.isBlank()) {
            try {
                dateFilter = LocalDate.parse(date);
            } catch (Exception e) {
            }
        }
        
        ReviewState stateFilter = null;
        if (state != null && !state.isBlank()) {
            try {
                stateFilter = ReviewState.valueOf(state);
            } catch (Exception e) {
            }
        }
        
        Page<Review> reviews = repository.findByCompanyNameAndFilters(companyName, flightNumber, keyword, dateFilter, stateFilter, pageable);
        return reviews.map(review -> {
            Flight flight = review.getFlightNumber() != null ? flightService.getFlightByFlightNumber(review.getFlightNumber()) : null;
            return new ReviewWithFlightDto(review, flight);
        });
    }

    public Optional<ReviewWithFlightDto> getReview(UUID reviewId) {
        return repository.findById(reviewId).map(review -> {
            Flight flight = review.getFlightNumber() != null ? flightService.getFlightByFlightNumber(review.getFlightNumber()) : null;
            return new ReviewWithFlightDto(review, flight);
        });
    }

    public Review respondToReview(UUID reviewId, String responseText, ReviewState newState) {
        Review r = repository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        r.setResponseText(responseText);
        r.setResponseAt(java.time.OffsetDateTime.now());
        r.setState(newState);
        return repository.save(r);
    }
}
