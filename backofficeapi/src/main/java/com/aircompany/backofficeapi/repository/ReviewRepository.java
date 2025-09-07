package com.aircompany.backofficeapi.repository;

import com.aircompany.backofficeapi.model.Review;
import com.aircompany.backofficeapi.model.ReviewState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByCompanyName(String companyName, Pageable pageable);
    Page<Review> findByCompanyNameAndState(String companyName, ReviewState state, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.companyName = :companyName AND " +
           "(:flightNumber IS NULL OR r.flightNumber LIKE %:flightNumber%) AND " +
           "(:keyword IS NULL OR r.description LIKE %:keyword%) AND " +
           "(:date IS NULL OR CAST(r.submittedAt AS date) = :date) AND " +
           "(:state IS NULL OR r.state = :state)")
    Page<Review> findByCompanyNameAndFilters(@Param("companyName") String companyName,
                                             @Param("flightNumber") String flightNumber,
                                             @Param("keyword") String keyword,
                                             @Param("date") LocalDate date,
                                             @Param("state") ReviewState state,
                                             Pageable pageable);
}
