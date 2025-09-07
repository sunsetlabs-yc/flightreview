package com.aircompany.backofficeapi.consumer;

import com.aircompany.backofficeapi.model.Review;
import com.aircompany.backofficeapi.model.ReviewState;
import com.aircompany.backofficeapi.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewConsumerTest {

    @Mock
    private ReviewRepository reviewRepository;

    private ReviewConsumer reviewConsumer;

    @BeforeEach
    void setUp() {
        reviewConsumer = new ReviewConsumer(reviewRepository);
    }

    @Test
    void handleNewReview_shouldUpdateReviewStateToTreated() {
        // Given
        UUID reviewId = UUID.randomUUID();
        Review review = new Review();
        review.setId(reviewId);
        review.setCustomerName("John Doe");
        review.setCustomerEmail("john@example.com");
        review.setFlightNumber("AF123");
        review.setCompanyName("Air France");
        review.setRating(5);
        review.setDescription("Great flight!");
        review.setSubmittedAt(OffsetDateTime.now());
        review.setState(ReviewState.SUBMITTED);

        Map<String, Object> event = new HashMap<>();
        event.put("reviewId", reviewId.toString());
        event.put("event", "NEW_REVIEW");
        event.put("customerName", "John Doe");
        event.put("customerEmail", "john@example.com");
        event.put("flightNumber", "AF123");
        event.put("companyName", "Air France");
        event.put("rating", 5);
        event.put("description", "Great flight!");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        reviewConsumer.handleNewReview(event);

        // Then
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(review);
        assertEquals(ReviewState.TREATED, review.getState());
    }

    @Test
    void handleNewReview_shouldNotUpdateWhenReviewNotFound() {
        // Given
        UUID reviewId = UUID.randomUUID();
        Map<String, Object> event = new HashMap<>();
        event.put("reviewId", reviewId.toString());
        event.put("event", "NEW_REVIEW");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When
        reviewConsumer.handleNewReview(event);

        // Then
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void handleNewReview_shouldHandleEventWithAdditionalFields() {
        // Given
        UUID reviewId = UUID.randomUUID();
        Review review = new Review();
        review.setId(reviewId);
        review.setCustomerName("John Doe");
        review.setCustomerEmail("john@example.com");
        review.setFlightNumber("AF123");
        review.setCompanyName("Air France");
        review.setRating(5);
        review.setDescription("Great flight!");
        review.setSubmittedAt(OffsetDateTime.now());
        review.setState(ReviewState.SUBMITTED);

        Map<String, Object> event = new HashMap<>();
        event.put("reviewId", reviewId.toString());
        event.put("event", "NEW_REVIEW");
        event.put("customerName", "John Doe");
        event.put("customerEmail", "john@example.com");
        event.put("flightNumber", "AF123");
        event.put("companyName", "Air France");
        event.put("origin", "New York");
        event.put("destination", "Paris");
        event.put("rating", 5);
        event.put("description", "Great flight!");
        event.put("submittedAt", OffsetDateTime.now().toString());
        event.put("additionalField", "additionalValue");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        reviewConsumer.handleNewReview(event);

        // Then
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(review);
        assertEquals(ReviewState.TREATED, review.getState());
    }

    @Test
    void handleNewReview_shouldHandleEventWithStringReviewId() {
        // Given
        UUID reviewId = UUID.randomUUID();
        Review review = new Review();
        review.setId(reviewId);
        review.setCustomerName("John Doe");
        review.setCustomerEmail("john@example.com");
        review.setFlightNumber("AF123");
        review.setCompanyName("Air France");
        review.setRating(5);
        review.setDescription("Great flight!");
        review.setSubmittedAt(OffsetDateTime.now());
        review.setState(ReviewState.SUBMITTED);

        Map<String, Object> event = new HashMap<>();
        event.put("reviewId", reviewId); // UUID object instead of string
        event.put("event", "NEW_REVIEW");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        reviewConsumer.handleNewReview(event);

        // Then
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(review);
        assertEquals(ReviewState.TREATED, review.getState());
    }

    @Test
    void handleNewReview_shouldHandleEmptyEvent() {
        // Given
        Map<String, Object> event = new HashMap<>();

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            reviewConsumer.handleNewReview(event);
        });

        verify(reviewRepository, never()).findById(any(UUID.class));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void handleNewReview_shouldHandleEventWithInvalidReviewId() {
        // Given
        Map<String, Object> event = new HashMap<>();
        event.put("reviewId", "invalid-uuid");
        event.put("event", "NEW_REVIEW");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewConsumer.handleNewReview(event);
        });

        verify(reviewRepository, never()).findById(any(UUID.class));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void handleNewReview_shouldHandleEventWithNullReviewId() {
        // Given
        Map<String, Object> event = new HashMap<>();
        event.put("reviewId", null);
        event.put("event", "NEW_REVIEW");

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            reviewConsumer.handleNewReview(event);
        });

        verify(reviewRepository, never()).findById(any(UUID.class));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void handleNewReview_shouldPreserveOtherReviewFields() {
        // Given
        UUID reviewId = UUID.randomUUID();
        Review review = new Review();
        review.setId(reviewId);
        review.setCustomerName("John Doe");
        review.setCustomerEmail("john@example.com");
        review.setFlightNumber("AF123");
        review.setCompanyName("Air France");
        review.setRating(5);
        review.setDescription("Great flight!");
        review.setSubmittedAt(OffsetDateTime.now());
        review.setState(ReviewState.SUBMITTED);

        Map<String, Object> event = new HashMap<>();
        event.put("reviewId", reviewId.toString());
        event.put("event", "NEW_REVIEW");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        reviewConsumer.handleNewReview(event);

        // Then
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(review);
        
        // Verify that only the state was changed
        assertEquals(ReviewState.TREATED, review.getState());
        assertEquals("John Doe", review.getCustomerName());
        assertEquals("john@example.com", review.getCustomerEmail());
        assertEquals("AF123", review.getFlightNumber());
        assertEquals("Air France", review.getCompanyName());
        assertEquals(5, review.getRating());
        assertEquals("Great flight!", review.getDescription());
        assertNotNull(review.getSubmittedAt());
    }
}
