package com.aircompany.backofficeapi.service;

import com.aircompany.backofficeapi.dto.ReviewWithFlightDto;
import com.aircompany.backofficeapi.model.Flight;
import com.aircompany.backofficeapi.model.Review;
import com.aircompany.backofficeapi.model.ReviewState;
import com.aircompany.backofficeapi.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
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
    private FlightService flightService;

    private ReviewService reviewService;

    private Review testReview;
    private Flight testFlight;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, flightService);

        testReview = new Review();
        testReview.setId(UUID.randomUUID());
        testReview.setCustomerName("John Doe");
        testReview.setCustomerEmail("john@example.com");
        testReview.setFlightNumber("AF123");
        testReview.setCompanyName("Air France");
        testReview.setRating(5);
        testReview.setDescription("Great flight!");
        testReview.setSubmittedAt(OffsetDateTime.now());
        testReview.setState(ReviewState.SUBMITTED);

        testFlight = new Flight();
        testFlight.setId(UUID.randomUUID());
        testFlight.setFlightNumber("AF123");
        testFlight.setCompanyName("Air France");
        testFlight.setOrigin("Paris");
        testFlight.setDestination("New York");
    }

    @Test
    void getReviewsForCompany_shouldReturnReviewsWithFlightInfo() {
        // Given
        String companyName = "Air France";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(testReview));
        
        when(reviewRepository.findByCompanyName(companyName, pageable)).thenReturn(reviewPage);
        when(flightService.getFlightByFlightNumber("AF123")).thenReturn(testFlight);

        // When
        Page<ReviewWithFlightDto> result = reviewService.getReviewsForCompany(companyName, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        
        ReviewWithFlightDto dto = result.getContent().get(0);
        assertEquals("John Doe", dto.getCustomerName());
        assertEquals("AF123", dto.getFlightNumber());
        assertEquals("Paris", dto.getOrigin());
        assertEquals("New York", dto.getDestination());
        
        verify(reviewRepository, times(1)).findByCompanyName(companyName, pageable);
        verify(flightService, times(1)).getFlightByFlightNumber("AF123");
    }

    @Test
    void getReviewsForCompany_shouldHandleNullFlightNumber() {
        // Given
        String companyName = "Air France";
        Pageable pageable = PageRequest.of(0, 10);
        testReview.setFlightNumber(null);
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(testReview));
        
        when(reviewRepository.findByCompanyName(companyName, pageable)).thenReturn(reviewPage);

        // When
        Page<ReviewWithFlightDto> result = reviewService.getReviewsForCompany(companyName, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        
        ReviewWithFlightDto dto = result.getContent().get(0);
        assertEquals("John Doe", dto.getCustomerName());
        assertNull(dto.getOrigin());
        assertNull(dto.getDestination());
        
        verify(reviewRepository, times(1)).findByCompanyName(companyName, pageable);
        verify(flightService, never()).getFlightByFlightNumber(anyString());
    }

    @Test
    void getReviewsForCompany_withFilters_shouldReturnFilteredReviews() {
        // Given
        String companyName = "Air France";
        Pageable pageable = PageRequest.of(0, 10);
        String flightNumber = "AF123";
        String keyword = "great";
        String date = "2024-01-15";
        String state = "SUBMITTED";
        
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(testReview));
        
        when(reviewRepository.findByCompanyNameAndFilters(
            eq(companyName), eq(flightNumber), eq(keyword), 
            eq(LocalDate.parse(date)), eq(ReviewState.SUBMITTED), eq(pageable)
        )).thenReturn(reviewPage);
        when(flightService.getFlightByFlightNumber("AF123")).thenReturn(testFlight);

        // When
        Page<ReviewWithFlightDto> result = reviewService.getReviewsForCompany(
            companyName, pageable, flightNumber, keyword, date, state);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        
        verify(reviewRepository, times(1)).findByCompanyNameAndFilters(
            eq(companyName), eq(flightNumber), eq(keyword), 
            eq(LocalDate.parse(date)), eq(ReviewState.SUBMITTED), eq(pageable)
        );
    }

    @Test
    void getReviewsForCompany_withInvalidDate_shouldIgnoreDateFilter() {
        // Given
        String companyName = "Air France";
        Pageable pageable = PageRequest.of(0, 10);
        String invalidDate = "invalid-date";
        
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(testReview));
        
        when(reviewRepository.findByCompanyNameAndFilters(
            eq(companyName), isNull(), isNull(), isNull(), isNull(), eq(pageable)
        )).thenReturn(reviewPage);

        // When
        Page<ReviewWithFlightDto> result = reviewService.getReviewsForCompany(
            companyName, pageable, null, null, invalidDate, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        
        verify(reviewRepository, times(1)).findByCompanyNameAndFilters(
            eq(companyName), isNull(), isNull(), isNull(), isNull(), eq(pageable)
        );
    }

    @Test
    void getReviewsForCompany_withInvalidState_shouldIgnoreStateFilter() {
        // Given
        String companyName = "Air France";
        Pageable pageable = PageRequest.of(0, 10);
        String invalidState = "INVALID_STATE";
        
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(testReview));
        
        when(reviewRepository.findByCompanyNameAndFilters(
            eq(companyName), isNull(), isNull(), isNull(), isNull(), eq(pageable)
        )).thenReturn(reviewPage);

        // When
        Page<ReviewWithFlightDto> result = reviewService.getReviewsForCompany(
            companyName, pageable, null, null, null, invalidState);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        
        verify(reviewRepository, times(1)).findByCompanyNameAndFilters(
            eq(companyName), isNull(), isNull(), isNull(), isNull(), eq(pageable)
        );
    }

    @Test
    void getReview_shouldReturnReviewWithFlightInfo() {
        // Given
        UUID reviewId = testReview.getId();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(flightService.getFlightByFlightNumber("AF123")).thenReturn(testFlight);

        // When
        Optional<ReviewWithFlightDto> result = reviewService.getReview(reviewId);

        // Then
        assertTrue(result.isPresent());
        ReviewWithFlightDto dto = result.get();
        assertEquals("John Doe", dto.getCustomerName());
        assertEquals("AF123", dto.getFlightNumber());
        assertEquals("Paris", dto.getOrigin());
        assertEquals("New York", dto.getDestination());
        
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(flightService, times(1)).getFlightByFlightNumber("AF123");
    }

    @Test
    void getReview_shouldReturnEmptyWhenReviewNotFound() {
        // Given
        UUID reviewId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When
        Optional<ReviewWithFlightDto> result = reviewService.getReview(reviewId);

        // Then
        assertFalse(result.isPresent());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(flightService, never()).getFlightByFlightNumber(anyString());
    }

    @Test
    void respondToReview_shouldUpdateReviewWithResponse() {
        // Given
        UUID reviewId = testReview.getId();
        String responseText = "Thank you for your feedback!";
        ReviewState newState = ReviewState.TREATED;
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Review result = reviewService.respondToReview(reviewId, responseText, newState);

        // Then
        assertNotNull(result);
        assertEquals(responseText, result.getResponseText());
        assertEquals(newState, result.getState());
        assertNotNull(result.getResponseAt());
        
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void respondToReview_shouldThrowExceptionWhenReviewNotFound() {
        // Given
        UUID reviewId = UUID.randomUUID();
        String responseText = "Thank you for your feedback!";
        ReviewState newState = ReviewState.TREATED;
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.respondToReview(reviewId, responseText, newState);
        });

        assertEquals("Review not found", exception.getMessage());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }
}
