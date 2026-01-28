package com.anz.tokenisation.controller;

import com.anz.tokenisation.service.TokenisationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Handle HTTP Requests and Responses.
@RestController
@RequestMapping
public class TokenisationController {

    private final TokenisationService tokenisationService;

    public TokenisationController(TokenisationService tokenisationService) {
        this.tokenisationService = tokenisationService;
    }

    /**
     * Tokenise endpoint - converts account numbers to tokens.
     */
    @PostMapping("/tokenise")
    public ResponseEntity<List<String>> tokenise(@RequestBody List<String> accountNumbers) {
        if (accountNumbers == null || accountNumbers.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<String> tokens = tokenisationService.tokenise(accountNumbers);
        return ResponseEntity.ok(tokens);
    }

    /**
     * Detokenise endpoint - converts tokens back to account numbers.
     */
    @PostMapping("/detokenise")
    public ResponseEntity<List<String>> detokenise(@RequestBody List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<String> accountNumbers = tokenisationService.detokenise(tokens);
        return ResponseEntity.ok(accountNumbers);
    }
}
