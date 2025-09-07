package com.aircompany.backofficeapi.controller;

import com.aircompany.backofficeapi.dto.ReviewWithFlightDto;
import com.aircompany.backofficeapi.model.Review;
import com.aircompany.backofficeapi.model.ReviewState;
import com.aircompany.backofficeapi.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReviewController(reviewService)).build();
    }

    @Test
    void getReviews_shouldReturnReviewsForCompany() throws Exception {
        // Given
        String companyName = "Air France";
        when(authentication.getName()).thenReturn(companyName);

        ReviewWithFlightDto dto = new ReviewWithFlightDto();
        dto.setId(UUID.randomUUID());
        dto.setCustomerName("John Doe");
        dto.setFlightNumber("AF123");
        dto.setRating(5);
        dto.setDescription("Great flight!");

        Page<ReviewWithFlightDto> reviewPage = new PageImpl<>(Arrays.asList(dto));
        when(reviewService.getReviewsForCompany(anyString(), any(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(reviewPage);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews")
                .principal(authentication)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$.content[0].flightNumber").value("AF123"));
    }

    @Test
    void getReviews_shouldReturnFilteredReviews() throws Exception {
        // Given
        String companyName = "Air France";
        when(authentication.getName()).thenReturn(companyName);

        ReviewWithFlightDto dto = new ReviewWithFlightDto();
        dto.setId(UUID.randomUUID());
        dto.setCustomerName("John Doe");
        dto.setFlightNumber("AF123");
        dto.setRating(5);

        Page<ReviewWithFlightDto> reviewPage = new PageImpl<>(Arrays.asList(dto));
        when(reviewService.getReviewsForCompany(anyString(), any(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(reviewPage);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews")
                .principal(authentication)
                .param("flightNumber", "AF123")
                .param("keyword", "great")
                .param("date", "2024-01-15")
                .param("state", "SUBMITTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getReview_shouldReturnReviewWhenFound() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        ReviewWithFlightDto dto = new ReviewWithFlightDto();
        dto.setId(reviewId);
        dto.setCustomerName("John Doe");
        dto.setFlightNumber("AF123");
        dto.setRating(5);

        when(reviewService.getReview(reviewId)).thenReturn(Optional.of(dto));

        // When & Then
        mockMvc.perform(get("/api/v1/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.flightNumber").value("AF123"));
    }

    @Test
    void getReview_shouldReturnNotFoundWhenReviewNotFound() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        when(reviewService.getReview(reviewId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/reviews/{id}", reviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    void respondToReview_shouldUpdateReviewWithResponse() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        String responseText = "Thank you for your feedback!";
        String newState = "RESPONDED";

        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        updatedReview.setResponseText(responseText);
        updatedReview.setState(ReviewState.TREATED);
        updatedReview.setResponseAt(OffsetDateTime.now());

        when(reviewService.respondToReview(reviewId, responseText, ReviewState.TREATED))
                .thenReturn(updatedReview);

        // When & Then
        mockMvc.perform(put("/api/v1/reviews/{id}/response", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .param("responseText", responseText)
                .param("newState", newState))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.responseText").value(responseText));
    }

    @Test
    void respondToReview_shouldReturnBadRequestForInvalidState() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        String responseText = "Thank you for your feedback!";
        String invalidState = "INVALID_STATE";

        // When & Then
        mockMvc.perform(put("/api/v1/reviews/{id}/response", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .param("responseText", responseText)
                .param("newState", invalidState))
                .andExpect(status().isBadRequest());
    }

    @Test
    void respondToReview_shouldReturnInternalServerErrorWhenReviewNotFound() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        String responseText = "Thank you for your feedback!";
        String newState = "RESPONDED";

        when(reviewService.respondToReview(reviewId, responseText, ReviewState.TREATED))
                .thenThrow(new RuntimeException("Review not found"));

        // When & Then
        mockMvc.perform(put("/api/v1/reviews/{id}/response", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .param("responseText", responseText)
                .param("newState", newState))
                .andExpect(status().isInternalServerError());
    }
}
