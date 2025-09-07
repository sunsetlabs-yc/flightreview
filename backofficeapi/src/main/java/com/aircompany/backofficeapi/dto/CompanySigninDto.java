package com.aircompany.backofficeapi.dto;

import jakarta.validation.constraints.NotBlank;

public class CompanySigninDto {
    @NotBlank
    private String name;
    @NotBlank
    private String password;

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }
}
