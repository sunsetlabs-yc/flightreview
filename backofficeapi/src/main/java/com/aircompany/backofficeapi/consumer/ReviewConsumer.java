package com.aircompany.backofficeapi.consumer;

import com.aircompany.backofficeapi.model.Review;
import com.aircompany.backofficeapi.model.ReviewState;
import com.aircompany.backofficeapi.repository.ReviewRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class ReviewConsumer {

    private final ReviewRepository repository;

    public ReviewConsumer(ReviewRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "reviews.new.queue")
    public void handleNewReview(Map<String, Object> event) {
        UUID reviewId = UUID.fromString(event.get("reviewId").toString());
        repository.findById(reviewId).ifPresent(r -> {
            r.setState(ReviewState.TREATED);
            repository.save(r);
        });
    }
}
