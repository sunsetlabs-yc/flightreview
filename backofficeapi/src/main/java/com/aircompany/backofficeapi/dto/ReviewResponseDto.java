package com.aircompany.backofficeapi.dto;

import com.aircompany.backofficeapi.model.ReviewState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewResponseDto {
    @NotBlank
    private String responseText;
    @NotNull
    private ReviewState newState;

    public @NotBlank String getResponseText() {
        return responseText;
    }

    public void setResponseText(@NotBlank String responseText) {
        this.responseText = responseText;
    }

    public @NotNull ReviewState getNewState() {
        return newState;
    }

    public void setNewState(@NotNull ReviewState newState) {
        this.newState = newState;
    }
}
