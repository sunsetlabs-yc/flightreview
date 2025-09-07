package com.aircompany.reviewapi.repository;

import com.aircompany.reviewapi.model.Review;
import com.aircompany.reviewapi.model.ReviewState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByState(ReviewState state, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.state = :state AND " +
           "(:flightNumber IS NULL OR r.flightNumber LIKE %:flightNumber%) AND " +
           "(:keyword IS NULL OR r.description LIKE %:keyword%) AND " +
           "(:date IS NULL OR CAST(r.submittedAt AS date) = :date)")
    Page<Review> findByStateAndFilters(@Param("state") ReviewState state,
                                       @Param("flightNumber") String flightNumber,
                                       @Param("keyword") String keyword,
                                       @Param("date") LocalDate date,
                                       Pageable pageable);
}
