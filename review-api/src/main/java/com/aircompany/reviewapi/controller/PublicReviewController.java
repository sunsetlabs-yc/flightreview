package com.aircompany.reviewapi.controller;

import com.aircompany.reviewapi.dto.ReviewSubmissionDto;
import com.aircompany.reviewapi.model.Flight;
import com.aircompany.reviewapi.model.Review;
import com.aircompany.reviewapi.model.ReviewState;
import com.aircompany.reviewapi.repository.ReviewRepository;
import com.aircompany.reviewapi.service.FlightService;
import com.aircompany.reviewapi.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicReviewController {
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final FlightService flightService;

    public PublicReviewController(ReviewService reviewService, ReviewRepository reviewRepository, FlightService flightService) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody @Valid ReviewSubmissionDto dto) {
        Review r = reviewService.submitReview(dto);
        return ResponseEntity.status(201).body(Map.of("id", r.getId()));
    }

    @GetMapping
    public Page<?> getPublicReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt,desc") String sort,
            @RequestParam(required = false) String flightNumber,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String date
    ) {
        Sort sortObj = Sort.by(Sort.Order.desc("submittedAt"));
        if (sort != null && !sort.isBlank()) {
            // simple parsing e.g. "submittedAt,desc"
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                sortObj = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
            }
        }
        Pageable p = PageRequest.of(page, size, sortObj);
        // only published reviews for public endpoint with optional filters
        LocalDate dateFilter = null;
        if (date != null && !date.isBlank()) {
            try {
                dateFilter = LocalDate.parse(date);
            } catch (Exception e) {
                // ignore invalid date format
            }
        }
        Page<Review> pageRes = reviewRepository.findByStateAndFilters(
            ReviewState.PUBLISHED, 
            flightNumber, 
            keyword, 
            dateFilter, 
            p
        );
        // map to public view (no customer information for privacy)
        Page<Object> publicPage = pageRes.map(r -> {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("id", r.getId());
            reviewMap.put("rating", r.getRating());
            reviewMap.put("description", r.getDescription());
            reviewMap.put("submittedAt", r.getSubmittedAt());
            reviewMap.put("companyResponse", r.getResponseText());
            
            // Get flight information if available
            if (r.getFlightNumber() != null) {
                Flight flight = flightService.getFlightByFlightNumber(r.getFlightNumber());
                if (flight != null) {
                    reviewMap.put("flightNumber", flight.getFlightNumber());
                    reviewMap.put("origin", flight.getOrigin());
                    reviewMap.put("destination", flight.getDestination());
                    reviewMap.put("flightDate", flight.getFlightDate());
                }
            }
            
            return reviewMap;
        });
        return publicPage;
    }

    @GetMapping("/flights")
    public ResponseEntity<List<Flight>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }
}
