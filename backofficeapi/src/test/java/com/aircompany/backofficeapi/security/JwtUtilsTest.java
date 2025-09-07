package com.aircompany.backofficeapi.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private final String testSecret = "testSecretKeyThatIsLongEnoughForHMACSHA256Algorithm";
    private final long testExpirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testExpirationMs);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        // Given
        String companyName = "Air France";

        // When
        String token = jwtUtils.generateToken(companyName);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void generateToken_shouldCreateTokenWithCorrectSubject() {
        // Given
        String companyName = "Air France";

        // When
        String token = jwtUtils.generateToken(companyName);
        String extractedCompanyName = jwtUtils.getCompanyNameFromToken(token);

        // Then
        assertEquals(companyName, extractedCompanyName);
    }

    @Test
    void generateToken_shouldCreateTokenWithExpiration() {
        // Given
        String companyName = "Air France";

        // When
        String token = jwtUtils.generateToken(companyName);

        // Then
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void getCompanyNameFromToken_shouldExtractCorrectCompanyName() {
        // Given
        String companyName = "Air France";
        String token = jwtUtils.generateToken(companyName);

        // When
        String extractedCompanyName = jwtUtils.getCompanyNameFromToken(token);

        // Then
        assertEquals(companyName, extractedCompanyName);
    }

    @Test
    void getCompanyNameFromToken_shouldThrowExceptionForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtUtils.getCompanyNameFromToken(invalidToken);
        });
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        // Given
        String companyName = "Air France";
        String token = jwtUtils.generateToken(companyName);

        // When
        boolean isValid = jwtUtils.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtils.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForNullToken() {
        // When
        boolean isValid = jwtUtils.validateToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForEmptyToken() {
        // When
        boolean isValid = jwtUtils.validateToken("");

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForMalformedToken() {
        // Given
        String malformedToken = "not.a.valid.jwt.token";

        // When
        boolean isValid = jwtUtils.validateToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void generateToken_shouldCreateDifferentTokensForSameCompany() {
        // Given
        String companyName = "Air France";

        // When
        String token1 = jwtUtils.generateToken(companyName);
        // Small delay to ensure different timestamps
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String token2 = jwtUtils.generateToken(companyName);

        // Then
        assertNotEquals(token1, token2);
        assertEquals(companyName, jwtUtils.getCompanyNameFromToken(token1));
        assertEquals(companyName, jwtUtils.getCompanyNameFromToken(token2));
    }

    @Test
    void generateToken_shouldHandleSpecialCharactersInCompanyName() {
        // Given
        String companyName = "Air France & Co. Ltd.";

        // When
        String token = jwtUtils.generateToken(companyName);
        String extractedCompanyName = jwtUtils.getCompanyNameFromToken(token);

        // Then
        assertEquals(companyName, extractedCompanyName);
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void generateToken_shouldHandleEmptyCompanyName() {
        // Given
        String companyName = "";

        // When
        String token = jwtUtils.generateToken(companyName);
        String extractedCompanyName = jwtUtils.getCompanyNameFromToken(token);

        // Then
        assertEquals(companyName, extractedCompanyName);
        assertTrue(jwtUtils.validateToken(token));
    }
}
