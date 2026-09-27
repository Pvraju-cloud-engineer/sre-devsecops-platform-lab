package com.example.claims;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/claims")
public class ClaimController {
    private final ClaimRepository claims;

    public ClaimController(ClaimRepository claims) {
        this.claims = claims;
    }

    @PostMapping
    public ResponseEntity<Claim> create(@Valid @RequestBody NewClaim request) {
        Claim saved = claims.save(new Claim(request.description()));
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> get(@PathVariable UUID id) {
        var claim = claims.findById(id);
        if (claim.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(claim.get());
    }

    public record NewClaim(@NotBlank String description) {
    }
}
