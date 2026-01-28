package com.anz.tokenisation.service;

import com.anz.tokenisation.entity.Token;
import com.anz.tokenisation.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

// Business Logic Layer.
@Service
public class TokenisationService {

    private static final int TOKEN_LENGTH = 32;
    private static final int BYTE_ARRAY_SIZE = (TOKEN_LENGTH * 3) / 4; // Base64 Math.
    private final TokenRepository tokenRepository;
    private final SecureRandom secureRandom;

    public TokenisationService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        // Use of SecureRandom - unpredictable tokens, low probability of duplicates, more secure than UUID or Random.
        this.secureRandom = new SecureRandom();
    }

    /**
     * Tokenises a list of account numbers.
     * If an account number already has a token, return existing token.
     * Otherwise, generate new unique token.
     */
    @Transactional
    public List<String> tokenise(List<String> accountNumbers) {
        List<String> tokens = new ArrayList<>();

        for (String accountNumber : accountNumbers) {
            String token = getOrCreateToken(accountNumber);
            tokens.add(token);
        }

        return tokens;
    }

    /**
     * Detokenises a list of tokens back to their original account numbers.
     */
    @Transactional(readOnly = true)
    public List<String> detokenise(List<String> tokens) {
        List<String> accountNumbers = new ArrayList<>();

        for (String tokenValue : tokens) {
            Optional<Token> tokenEntity = tokenRepository.findByToken(tokenValue);
            if (tokenEntity.isPresent()) {
                accountNumbers.add(tokenEntity.get().getAccountNumber());
            } else {
                // Add null for tokens that don't exist.
                // For production I'd throw an exception or handle it differently.
                accountNumbers.add(null);
            }
        }

        return accountNumbers;
    }

    /**
     * Get existing token for an account number or create a new one.
     */
    private String getOrCreateToken(String accountNumber) {
        Optional<Token> existingToken = tokenRepository.findByAccountNumber(accountNumber);
        
        if (existingToken.isPresent()) {
            return existingToken.get().getToken();
        }

        String newToken = generateUniqueToken();
        Token token = new Token(newToken, accountNumber);
        tokenRepository.save(token);
        
        return newToken;
    }

    /**
     * Generate a unique random token.
     * Uses Base64 URL safe encoding for a clean alpha numeric string.
     */
    private String generateUniqueToken() {
        String token;
        do {
            byte[] randomBytes = new byte[BYTE_ARRAY_SIZE]; // 24 bytes = 32 chars in Base64.
            secureRandom.nextBytes(randomBytes);
            token = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(randomBytes);
        } while (tokenRepository.findByToken(token).isPresent());
        
        return token;
    }
}
