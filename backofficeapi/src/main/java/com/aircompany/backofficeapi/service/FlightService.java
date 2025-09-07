package com.aircompany.backofficeapi.service;

import com.aircompany.backofficeapi.model.Flight;
import com.aircompany.backofficeapi.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FlightService {
    
    @Autowired
    private FlightRepository flightRepository;
    
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }
    
    public Flight getFlightById(UUID id) {
        return flightRepository.findById(id).orElse(null);
    }
    
    public List<Flight> getFlightsByCompanyName(String companyName) {
        return flightRepository.findByCompanyName(companyName);
    }
    
    public Flight getFlightByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }
}
