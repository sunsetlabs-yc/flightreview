package com.aircompany.reviewapi.service;

import com.aircompany.reviewapi.config.RabbitConfig;
import com.aircompany.reviewapi.dto.ReviewSubmissionDto;
import com.aircompany.reviewapi.model.Flight;
import com.aircompany.reviewapi.model.Review;
import com.aircompany.reviewapi.model.ReviewState;
import com.aircompany.reviewapi.repository.ReviewRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RabbitTemplate rabbitTemplate;
    private final FlightService flightService;

    public ReviewService(ReviewRepository reviewRepository, RabbitTemplate rabbitTemplate, FlightService flightService) {
        this.reviewRepository = reviewRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.flightService = flightService;
    }

    public Review submitReview(ReviewSubmissionDto dto) {
        // Get flight information
        Flight flight = flightService.getFlightByFlightNumber(dto.getFlightNumber());
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found with number: " + dto.getFlightNumber());
        }

        Review r = new Review();
        r.setId(UUID.randomUUID());
        r.setCustomerName(dto.getCustomerName());
        r.setCustomerEmail(dto.getCustomerEmail());
        r.setFlightNumber(dto.getFlightNumber());
        r.setCompanyName(flight.getCompanyName());
        r.setRating(dto.getRating());
        r.setDescription(dto.getDescription());
        r.setSubmittedAt(OffsetDateTime.now());
        r.setState(ReviewState.SUBMITTED);

        r = reviewRepository.save(r);

        Map<String, Object> event = new HashMap<>();
        event.put("event", "NEW_REVIEW");
        event.put("reviewId", r.getId());
        event.put("customerName", r.getCustomerName());
        event.put("customerEmail", r.getCustomerEmail());
        event.put("flightNumber", r.getFlightNumber());
        event.put("companyName", r.getCompanyName());
        event.put("origin", flight.getOrigin());
        event.put("destination", flight.getDestination());
        event.put("rating", r.getRating());
        event.put("description", r.getDescription());
        event.put("submittedAt", r.getSubmittedAt());

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.NEW_REVIEW_ROUTING_KEY, event);

        return r;
    }
}
