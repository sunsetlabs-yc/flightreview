package com.aircompany.reviewapi.service;

import com.aircompany.reviewapi.config.RabbitConfig;
import com.aircompany.reviewapi.dto.ReviewSubmissionDto;
import com.aircompany.reviewapi.model.Flight;
import com.aircompany.reviewapi.model.Review;
import com.aircompany.reviewapi.model.ReviewState;
import com.aircompany.reviewapi.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private FlightService flightService;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, rabbitTemplate, flightService);
    }

    @Test
    void submitReview_savesAndPublishesEvent() {
        // Given
        ReviewSubmissionDto dto = new ReviewSubmissionDto();
        dto.setCustomerName("Alice");
        dto.setCustomerEmail("alice@example.com");
        dto.setFlightNumber("AF123");
        dto.setRating(5);
        dto.setDescription("Great flight.");

        Flight flight = new Flight();
        flight.setFlightNumber("AF123");
        flight.setCompanyName("Air France");
        flight.setOrigin("New York");
        flight.setDestination("Paris");

        when(flightService.getFlightByFlightNumber("AF123")).thenReturn(flight);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Review saved = reviewService.submitReview(dto);

        // Then
        assertNotNull(saved.getId());
        assertEquals("AF123", saved.getFlightNumber());
        assertEquals("Air France", saved.getCompanyName());
        assertEquals("Alice", saved.getCustomerName());
        assertEquals("alice@example.com", saved.getCustomerEmail());
        assertEquals(5, saved.getRating());
        assertEquals("Great flight.", saved.getDescription());
        assertEquals(ReviewState.SUBMITTED, saved.getState());
        assertNotNull(saved.getSubmittedAt());

        verify(flightService, times(1)).getFlightByFlightNumber("AF123");
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitConfig.EXCHANGE), eq(RabbitConfig.NEW_REVIEW_ROUTING_KEY), any(Map.class));
    }

    @Test
    void submitReview_shouldThrowExceptionWhenFlightNotFound() {
        // Given
        ReviewSubmissionDto dto = new ReviewSubmissionDto();
        dto.setCustomerName("Alice");
        dto.setCustomerEmail("alice@example.com");
        dto.setFlightNumber("NONEXISTENT");
        dto.setRating(5);
        dto.setDescription("Great flight.");

        when(flightService.getFlightByFlightNumber("NONEXISTENT")).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.submitReview(dto);
        });

        assertEquals("Flight not found with number: NONEXISTENT", exception.getMessage());
        verify(flightService, times(1)).getFlightByFlightNumber("NONEXISTENT");
        verify(reviewRepository, never()).save(any(Review.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Map.class));
    }

    @Test
    void submitReview_shouldPublishCorrectEventData() {
        // Given
        ReviewSubmissionDto dto = new ReviewSubmissionDto();
        dto.setCustomerName("Alice");
        dto.setCustomerEmail("alice@example.com");
        dto.setFlightNumber("AF123");
        dto.setRating(5);
        dto.setDescription("Great flight.");

        Flight flight = new Flight();
        flight.setFlightNumber("AF123");
        flight.setCompanyName("Air France");
        flight.setOrigin("New York");
        flight.setDestination("Paris");

        when(flightService.getFlightByFlightNumber("AF123")).thenReturn(flight);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        // When
        reviewService.submitReview(dto);

        // Then
        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitConfig.EXCHANGE), 
            eq(RabbitConfig.NEW_REVIEW_ROUTING_KEY), 
            eventCaptor.capture()
        );

        // Verify the event contains the correct data
        assertNotNull(eventCaptor.getValue());
    }

    @Test
    void submitReview_shouldSetCorrectReviewState() {
        // Given
        ReviewSubmissionDto dto = new ReviewSubmissionDto();
        dto.setCustomerName("Alice");
        dto.setCustomerEmail("alice@example.com");
        dto.setFlightNumber("AF123");
        dto.setRating(5);
        dto.setDescription("Great flight.");

        Flight flight = new Flight();
        flight.setFlightNumber("AF123");
        flight.setCompanyName("Air France");

        when(flightService.getFlightByFlightNumber("AF123")).thenReturn(flight);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Review saved = reviewService.submitReview(dto);

        // Then
        assertEquals(ReviewState.SUBMITTED, saved.getState());
    }

    @Test
    void submitReview_shouldSetSubmittedAtTimestamp() {
        // Given
        ReviewSubmissionDto dto = new ReviewSubmissionDto();
        dto.setCustomerName("Alice");
        dto.setCustomerEmail("alice@example.com");
        dto.setFlightNumber("AF123");
        dto.setRating(5);
        dto.setDescription("Great flight.");

        Flight flight = new Flight();
        flight.setFlightNumber("AF123");
        flight.setCompanyName("Air France");

        when(flightService.getFlightByFlightNumber("AF123")).thenReturn(flight);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OffsetDateTime beforeSubmission = OffsetDateTime.now();

        // When
        Review saved = reviewService.submitReview(dto);

        // Then
        assertNotNull(saved.getSubmittedAt());
        assertTrue(saved.getSubmittedAt().isAfter(beforeSubmission.minusSeconds(1)));
        assertTrue(saved.getSubmittedAt().isBefore(OffsetDateTime.now().plusSeconds(1)));
    }
}
