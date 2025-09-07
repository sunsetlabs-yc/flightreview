package com.aircompany.backofficeapi.controller;

import com.aircompany.backofficeapi.dto.CompanySigninDto;
import com.aircompany.backofficeapi.dto.CompanySignupDto;
import com.aircompany.backofficeapi.model.Company;
import com.aircompany.backofficeapi.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CompanyController(companyService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void signup_shouldReturnCreatedStatusWithCompanyId() throws Exception {
        // Given
        CompanySignupDto dto = new CompanySignupDto();
        dto.setName("Air France");
        dto.setEmail("contact@airfrance.com");
        dto.setPassword("password123");

        Company savedCompany = new Company();
        UUID companyId = UUID.randomUUID();
        savedCompany.setId(companyId);
        savedCompany.setName("Air France");
        savedCompany.setEmail("contact@airfrance.com");
        savedCompany.setCreatedAt(OffsetDateTime.now());

        when(companyService.signup(any(CompanySignupDto.class))).thenReturn(savedCompany);

        // When & Then
        mockMvc.perform(post("/api/v1/company/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(companyId.toString()));
    }

    @Test
    void signup_shouldReturnBadRequestForInvalidData() throws Exception {
        // Given
        CompanySignupDto dto = new CompanySignupDto();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/company/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signin_shouldReturnTokenForValidCredentials() throws Exception {
        // Given
        CompanySigninDto dto = new CompanySigninDto();
        dto.setName("Air France");
        dto.setPassword("password123");

        String token = "jwt-token";
        when(companyService.signin(any(CompanySigninDto.class))).thenReturn(token);

        // When & Then
        mockMvc.perform(post("/api/v1/company/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void signin_shouldReturnBadRequestForInvalidData() throws Exception {
        // Given
        CompanySigninDto dto = new CompanySigninDto();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/company/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signin_shouldReturnInternalServerErrorForInvalidCredentials() throws Exception {
        // Given
        CompanySigninDto dto = new CompanySigninDto();
        dto.setName("Air France");
        dto.setPassword("wrongpassword");

        when(companyService.signin(any(CompanySigninDto.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/v1/company/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
}
