package com.aircompany.backofficeapi.service;

import com.aircompany.backofficeapi.dto.CompanySigninDto;
import com.aircompany.backofficeapi.dto.CompanySignupDto;
import com.aircompany.backofficeapi.model.Company;
import com.aircompany.backofficeapi.repository.CompanyRepository;
import com.aircompany.backofficeapi.security.JwtUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyService {
    private final CompanyRepository repository;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CompanyService(CompanyRepository repository, JwtUtils jwtUtils) {
        this.repository = repository;
        this.jwtUtils = jwtUtils;
    }

    public Company signup(CompanySignupDto dto) {
        Company c = new Company();
        c.setId(UUID.randomUUID());
        c.setName(dto.getName());
        c.setEmail(dto.getEmail());
        c.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        c.setCreatedAt(OffsetDateTime.now());
        return repository.save(c);
    }

    public String signin(CompanySigninDto dto) {
        Optional<Company> opt = repository.findByName(dto.getName());
        if (opt.isPresent() && passwordEncoder.matches(dto.getPassword(), opt.get().getPasswordHash())) {
            return jwtUtils.generateToken(opt.get().getName());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
