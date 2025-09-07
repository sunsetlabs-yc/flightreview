package com.aircompany.backofficeapi.dto;

import com.aircompany.backofficeapi.model.Review;
import com.aircompany.backofficeapi.model.ReviewState;
import com.aircompany.backofficeapi.model.Flight;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ReviewWithFlightDto {
    private UUID id;
    private String customerName;
    private String customerEmail;
    private String flightNumber;
    private String companyName;
    private Integer rating;
    private String description;
    private OffsetDateTime submittedAt;
    private ReviewState state;
    private String responseText;
    private OffsetDateTime responseAt;
    
    private String origin;
    private String destination;
    private String flightDate;

    public ReviewWithFlightDto() {}

    public ReviewWithFlightDto(Review review, Flight flight) {
        this.id = review.getId();
        this.customerName = review.getCustomerName();
        this.customerEmail = review.getCustomerEmail();
        this.flightNumber = review.getFlightNumber();
        this.companyName = review.getCompanyName();
        this.rating = review.getRating();
        this.description = review.getDescription();
        this.submittedAt = review.getSubmittedAt();
        this.state = review.getState();
        this.responseText = review.getResponseText();
        this.responseAt = review.getResponseAt();
        
        if (flight != null) {
            this.origin = flight.getOrigin();
            this.destination = flight.getDestination();
            this.flightDate = flight.getFlightDate().toString();
        }
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }

    public ReviewState getState() { return state; }
    public void setState(ReviewState state) { this.state = state; }

    public String getResponseText() { return responseText; }
    public void setResponseText(String responseText) { this.responseText = responseText; }

    public OffsetDateTime getResponseAt() { return responseAt; }
    public void setResponseAt(OffsetDateTime responseAt) { this.responseAt = responseAt; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getFlightDate() { return flightDate; }
    public void setFlightDate(String flightDate) { this.flightDate = flightDate; }
}
