package com.aircompany.backofficeapi.repository;

import com.aircompany.backofficeapi.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByEmail(String email);
    Optional<Company> findByName(String name);
}
