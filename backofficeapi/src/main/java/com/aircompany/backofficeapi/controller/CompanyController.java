package com.aircompany.backofficeapi.controller;

import com.aircompany.backofficeapi.dto.CompanySigninDto;
import com.aircompany.backofficeapi.dto.CompanySignupDto;
import com.aircompany.backofficeapi.model.Company;
import com.aircompany.backofficeapi.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/company")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid CompanySignupDto dto) {
        Company c = service.signup(dto);
        return ResponseEntity.status(201).body(c.getId());
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody @Valid CompanySigninDto dto) {
        String token = service.signin(dto);
        return ResponseEntity.ok().body(Map.of("token", token));
    }
}
