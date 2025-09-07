package com.aircompany.reviewapi.service;

import com.aircompany.reviewapi.dto.ReviewSubmissionDto;
import com.aircompany.reviewapi.model.Review;
import com.aircompany.reviewapi.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {
    @Test
    void submitReview_savesAndPublishesEvent() {
        ReviewRepository repo = mock(ReviewRepository.class);
        RabbitTemplate template = mock(RabbitTemplate.class);

        ReviewService service = new ReviewService(repo, template);

        ReviewSubmissionDto dto = new ReviewSubmissionDto();
        dto.setCustomerName("Alice");
        dto.setCustomerEmail("alice@example.com");
        dto.setFlightNumber("AF123");
        dto.setDestination("Paris");
        dto.setCompanyName("Air France");
        dto.setRating(5);
        dto.setDescription("Great flight.");

        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Review saved = service.submitReview(dto);

        assertNotNull(saved.getId());
        assertEquals("AF123", saved.getFlightNumber());
        verify(repo, times(1)).save(any());
        verify(template, times(1)).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }
}
