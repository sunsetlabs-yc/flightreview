package com.aircompany.reviewapi.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

public class ReviewSubmissionDto {
    @NotBlank
    private String customerName;

    @Email
    @NotBlank
    private String customerEmail;

    @NotBlank
    private String flightNumber;

    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank
    private String description;

    public @NotBlank String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(@NotBlank String customerName) {
        this.customerName = customerName;
    }

    public @Email @NotBlank String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(@Email @NotBlank String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public @Min(1) @Max(5) Integer getRating() {
        return rating;
    }

    public void setRating(@Min(1) @Max(5) Integer rating) {
        this.rating = rating;
    }

    public @NotBlank String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank String description) {
        this.description = description;
    }


}
