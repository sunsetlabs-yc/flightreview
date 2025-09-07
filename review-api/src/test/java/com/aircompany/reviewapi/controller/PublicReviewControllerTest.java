package com.aircompany.reviewapi.controller;

import com.aircompany.reviewapi.dto.ReviewSubmissionDto;
import com.aircompany.reviewapi.model.Flight;
import com.aircompany.reviewapi.model.Review;
import com.aircompany.reviewapi.repository.ReviewRepository;
import com.aircompany.reviewapi.service.FlightService;
import com.aircompany.reviewapi.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PublicReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private FlightService flightService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
            new PublicReviewController(reviewService, reviewRepository, flightService)
        ).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void submitReview_shouldReturnCreatedStatusWithReviewId() throws Exception {
        // Given
        ReviewSubmissionDto dto = new ReviewSubmissionDto();
        dto.setCustomerName("John Doe");
        dto.setCustomerEmail("john@example.com");
        dto.setFlightNumber("AF123");
        dto.setRating(5);
        dto.setDescription("Great flight!");

        Review savedReview = new Review();
        UUID reviewId = UUID.randomUUID();
        savedReview.setId(reviewId);
        savedReview.setCustomerName("John Doe");
        savedReview.setCustomerEmail("john@example.com");
        savedReview.setFlightNumber("AF123");
        savedReview.setRating(5);
        savedReview.setDescription("Great flight!");
        savedReview.setSubmittedAt(OffsetDateTime.now());

        when(reviewService.submitReview(any(ReviewSubmissionDto.class))).thenReturn(savedReview);

        // When & Then
        mockMvc.perform(post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reviewId.toString()));
    }

    @Test
    void submitReview_shouldReturnBadRequestForInvalidData() throws Exception {
        // Given
        ReviewSubmissionDto dto = new ReviewSubmissionDto();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitReview_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {
        // Given
        ReviewSubmissionDto dto = new ReviewSubmissionDto();
        dto.setCustomerName("John Doe");
        dto.setCustomerEmail("john@example.com");
        dto.setFlightNumber("AF123");
        dto.setRating(5);
        dto.setDescription("Great flight!");

        when(reviewService.submitReview(any(ReviewSubmissionDto.class)))
                .thenThrow(new IllegalArgumentException("Flight not found"));

        // When & Then
        mockMvc.perform(post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPublicReviews_shouldReturnReviewsWithDefaultPagination() throws Exception {
        // Given
        Review review = new Review();
        review.setId(UUID.randomUUID());
        review.setCustomerName("John Doe");
        review.setFlightNumber("AF123");
        review.setRating(5);
        review.setDescription("Great flight!");
        review.setSubmittedAt(OffsetDateTime.now());

        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(review));
        when(reviewRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(reviewPage);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$.content[0].flightNumber").value("AF123"));
    }

    @Test
    void getPublicReviews_shouldReturnReviewsWithCustomPagination() throws Exception {
        // Given
        Review review = new Review();
        review.setId(UUID.randomUUID());
        review.setCustomerName("John Doe");
        review.setFlightNumber("AF123");
        review.setRating(5);
        review.setDescription("Great flight!");
        review.setSubmittedAt(OffsetDateTime.now());

        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(review));
        when(reviewRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(reviewPage);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "submittedAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].customerName").value("John Doe"));
    }

    @Test
    void getPublicReviews_shouldReturnReviewsWithFlightNumberFilter() throws Exception {
        // Given
        Review review = new Review();
        review.setId(UUID.randomUUID());
        review.setCustomerName("John Doe");
        review.setFlightNumber("AF123");
        review.setRating(5);
        review.setDescription("Great flight!");
        review.setSubmittedAt(OffsetDateTime.now());

        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(review));
        when(reviewRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(reviewPage);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews")
                .param("flightNumber", "AF123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].flightNumber").value("AF123"));
    }

    @Test
    void getPublicReviews_shouldReturnReviewsWithKeywordFilter() throws Exception {
        // Given
        Review review = new Review();
        review.setId(UUID.randomUUID());
        review.setCustomerName("John Doe");
        review.setFlightNumber("AF123");
        review.setRating(5);
        review.setDescription("Great flight!");
        review.setSubmittedAt(OffsetDateTime.now());

        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(review));
        when(reviewRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(reviewPage);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews")
                .param("keyword", "great"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].description").value("Great flight!"));
    }

    @Test
    void getPublicReviews_shouldReturnEmptyPageWhenNoReviews() throws Exception {
        // Given
        Page<Review> emptyPage = new PageImpl<>(Arrays.asList());
        when(reviewRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void getPublicReviews_shouldHandleInvalidSortParameter() throws Exception {
        // Given
        Review review = new Review();
        review.setId(UUID.randomUUID());
        review.setCustomerName("John Doe");
        review.setFlightNumber("AF123");
        review.setRating(5);
        review.setDescription("Great flight!");
        review.setSubmittedAt(OffsetDateTime.now());

        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(review));
        when(reviewRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(reviewPage);

        // When & Then
        mockMvc.perform(get("/api/v1/reviews")
                .param("sort", "invalidField,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
