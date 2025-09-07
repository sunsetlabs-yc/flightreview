package com.aircompany.backofficeapi.service;

import com.aircompany.backofficeapi.dto.CompanySigninDto;
import com.aircompany.backofficeapi.dto.CompanySignupDto;
import com.aircompany.backofficeapi.model.Company;
import com.aircompany.backofficeapi.repository.CompanyRepository;
import com.aircompany.backofficeapi.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private JwtUtils jwtUtils;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepository, jwtUtils);
    }

    @Test
    void signup_shouldCreateAndSaveCompany() {
        // Given
        CompanySignupDto dto = new CompanySignupDto();
        dto.setName("Air France");
        dto.setEmail("contact@airfrance.com");
        dto.setPassword("password123");

        Company savedCompany = new Company();
        savedCompany.setId(UUID.randomUUID());
        savedCompany.setName("Air France");
        savedCompany.setEmail("contact@airfrance.com");
        savedCompany.setCreatedAt(OffsetDateTime.now());

        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        // When
        Company result = companyService.signup(dto);

        // Then
        assertNotNull(result);
        assertEquals("Air France", result.getName());
        assertEquals("contact@airfrance.com", result.getEmail());
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getPasswordHash());
        assertTrue(result.getPasswordHash().startsWith("$2a$")); // BCrypt hash format

        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void signup_shouldEncodePassword() {
        // Given
        CompanySignupDto dto = new CompanySignupDto();
        dto.setName("Air France");
        dto.setEmail("contact@airfrance.com");
        dto.setPassword("password123");

        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company company = invocation.getArgument(0);
            return company;
        });

        // When
        Company result = companyService.signup(dto);

        // Then
        assertNotNull(result.getPasswordHash());
        assertNotEquals("password123", result.getPasswordHash());
        
        // Verify password is properly encoded by checking it can be matched
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("password123", result.getPasswordHash()));
    }

    @Test
    void signin_shouldReturnTokenForValidCredentials() {
        // Given
        CompanySigninDto dto = new CompanySigninDto();
        dto.setName("Air France");
        dto.setPassword("password123");

        Company company = new Company();
        company.setName("Air France");
        company.setPasswordHash(new BCryptPasswordEncoder().encode("password123"));

        when(companyRepository.findByName("Air France")).thenReturn(Optional.of(company));
        when(jwtUtils.generateToken("Air France")).thenReturn("jwt-token");

        // When
        String token = companyService.signin(dto);

        // Then
        assertEquals("jwt-token", token);
        verify(companyRepository, times(1)).findByName("Air France");
        verify(jwtUtils, times(1)).generateToken("Air France");
    }

    @Test
    void signin_shouldThrowExceptionForInvalidUsername() {
        // Given
        CompanySigninDto dto = new CompanySigninDto();
        dto.setName("NonExistent");
        dto.setPassword("password123");

        when(companyRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.signin(dto);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(companyRepository, times(1)).findByName("NonExistent");
        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    void signin_shouldThrowExceptionForInvalidPassword() {
        // Given
        CompanySigninDto dto = new CompanySigninDto();
        dto.setName("Air France");
        dto.setPassword("wrongpassword");

        Company company = new Company();
        company.setName("Air France");
        company.setPasswordHash(new BCryptPasswordEncoder().encode("password123"));

        when(companyRepository.findByName("Air France")).thenReturn(Optional.of(company));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.signin(dto);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(companyRepository, times(1)).findByName("Air France");
        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    void signin_shouldThrowExceptionWhenCompanyNotFound() {
        // Given
        CompanySigninDto dto = new CompanySigninDto();
        dto.setName("Air France");
        dto.setPassword("password123");

        when(companyRepository.findByName("Air France")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.signin(dto);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(companyRepository, times(1)).findByName("Air France");
        verify(jwtUtils, never()).generateToken(anyString());
    }
}
