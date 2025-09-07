package com.aircompany.backofficeapi.service;

import com.aircompany.backofficeapi.model.Flight;
import com.aircompany.backofficeapi.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setId(UUID.randomUUID());
        testFlight.setFlightNumber("AF123");
        testFlight.setCompanyName("Air France");
        testFlight.setOrigin("Paris");
        testFlight.setDestination("New York");
        testFlight.setCreatedAt(OffsetDateTime.now());
    }

    @Test
    void getAllFlights_shouldReturnAllFlights() {
        // Given
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightRepository.findAll()).thenReturn(flights);

        // When
        List<Flight> result = flightService.getAllFlights();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AF123", result.get(0).getFlightNumber());
        verify(flightRepository, times(1)).findAll();
    }

    @Test
    void getAllFlights_shouldReturnEmptyListWhenNoFlights() {
        // Given
        when(flightRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Flight> result = flightService.getAllFlights();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(flightRepository, times(1)).findAll();
    }

    @Test
    void getFlightById_shouldReturnFlightWhenFound() {
        // Given
        UUID flightId = testFlight.getId();
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(testFlight));

        // When
        Flight result = flightService.getFlightById(flightId);

        // Then
        assertNotNull(result);
        assertEquals(flightId, result.getId());
        assertEquals("AF123", result.getFlightNumber());
        verify(flightRepository, times(1)).findById(flightId);
    }

    @Test
    void getFlightById_shouldReturnNullWhenNotFound() {
        // Given
        UUID flightId = UUID.randomUUID();
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        // When
        Flight result = flightService.getFlightById(flightId);

        // Then
        assertNull(result);
        verify(flightRepository, times(1)).findById(flightId);
    }

    @Test
    void getFlightsByCompanyName_shouldReturnFlightsForCompany() {
        // Given
        String companyName = "Air France";
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightRepository.findByCompanyName(companyName)).thenReturn(flights);

        // When
        List<Flight> result = flightService.getFlightsByCompanyName(companyName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Air France", result.get(0).getCompanyName());
        verify(flightRepository, times(1)).findByCompanyName(companyName);
    }

    @Test
    void getFlightsByCompanyName_shouldReturnEmptyListWhenNoFlightsForCompany() {
        // Given
        String companyName = "NonExistent";
        when(flightRepository.findByCompanyName(companyName)).thenReturn(Arrays.asList());

        // When
        List<Flight> result = flightService.getFlightsByCompanyName(companyName);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(flightRepository, times(1)).findByCompanyName(companyName);
    }

    @Test
    void getFlightByFlightNumber_shouldReturnFlightWhenFound() {
        // Given
        String flightNumber = "AF123";
        when(flightRepository.findByFlightNumber(flightNumber)).thenReturn(testFlight);

        // When
        Flight result = flightService.getFlightByFlightNumber(flightNumber);

        // Then
        assertNotNull(result);
        assertEquals(flightNumber, result.getFlightNumber());
        verify(flightRepository, times(1)).findByFlightNumber(flightNumber);
    }

    @Test
    void getFlightByFlightNumber_shouldReturnNullWhenNotFound() {
        // Given
        String flightNumber = "NONEXISTENT";
        when(flightRepository.findByFlightNumber(flightNumber)).thenReturn(null);

        // When
        Flight result = flightService.getFlightByFlightNumber(flightNumber);

        // Then
        assertNull(result);
        verify(flightRepository, times(1)).findByFlightNumber(flightNumber);
    }
}
