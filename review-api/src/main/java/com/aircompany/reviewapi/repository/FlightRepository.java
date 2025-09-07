package com.aircompany.reviewapi.repository;

import com.aircompany.reviewapi.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {
    List<Flight> findByCompanyName(String companyName);
    Flight findByFlightNumber(String flightNumber);
}
