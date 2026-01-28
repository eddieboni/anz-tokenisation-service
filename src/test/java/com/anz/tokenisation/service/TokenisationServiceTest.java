package com.anz.tokenisation.service;

import com.anz.tokenisation.entity.Token;
import com.anz.tokenisation.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Test Service Layer in Isolation Using Mock Object.
@ExtendWith(MockitoExtension.class)
class TokenisationServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenisationService tokenisationService;

    @BeforeEach
    void setUp() {
        // Reset any state if needed
    }

    @Test
    void testTokenise_NewAccountNumbers_CreatesNewTokens() {
        // Arrange
        List<String> accountNumbers = Arrays.asList(
                "4111-1111-1111-1111",
                "4444-3333-2222-1111"
        );

        when(tokenRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());
        when(tokenRepository.findByToken(anyString()))
                .thenReturn(Optional.empty());
        when(tokenRepository.save(any(Token.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<String> tokens = tokenisationService.tokenise(accountNumbers);

        // Assert
        assertNotNull(tokens);
        assertEquals(2, tokens.size());
        assertEquals(32, tokens.get(0).length());
        assertEquals(32, tokens.get(1).length());
        assertNotEquals(tokens.get(0), tokens.get(1));
        verify(tokenRepository, times(2)).save(any(Token.class));
    }

    @Test
    void testTokenise_ExistingAccountNumber_ReturnsExistingToken() {
        // Arrange
        String accountNumber = "4111-1111-1111-1111";
        String existingToken = "fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy";
        Token token = new Token(existingToken, accountNumber);

        when(tokenRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(token));

        // Act
        List<String> tokens = tokenisationService.tokenise(Arrays.asList(accountNumber));

        // Assert
        assertNotNull(tokens);
        assertEquals(1, tokens.size());
        assertEquals(existingToken, tokens.get(0));
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testTokenise_MixedNewAndExisting_HandlesCorrectly() {
        // Arrange
        String existingAccount = "4111-1111-1111-1111";
        String newAccount = "4444-3333-2222-1111";
        String existingToken = "fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy";

        when(tokenRepository.findByAccountNumber(existingAccount))
                .thenReturn(Optional.of(new Token(existingToken, existingAccount)));
        when(tokenRepository.findByAccountNumber(newAccount))
                .thenReturn(Optional.empty());
        when(tokenRepository.findByToken(anyString()))
                .thenReturn(Optional.empty());
        when(tokenRepository.save(any(Token.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<String> tokens = tokenisationService.tokenise(
                Arrays.asList(existingAccount, newAccount)
        );

        // Assert
        assertNotNull(tokens);
        assertEquals(2, tokens.size());
        assertEquals(existingToken, tokens.get(0));
        assertNotEquals(existingToken, tokens.get(1));
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void testDetokenise_ExistingTokens_ReturnsAccountNumbers() {
        // Arrange
        String token1 = "fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy";
        String token2 = "L4hKuBJHxe67ENSKLVbdIH8NhFefPui2";
        String account1 = "4111-1111-1111-1111";
        String account2 = "4444-3333-2222-1111";

        when(tokenRepository.findByToken(token1))
                .thenReturn(Optional.of(new Token(token1, account1)));
        when(tokenRepository.findByToken(token2))
                .thenReturn(Optional.of(new Token(token2, account2)));

        // Act
        List<String> accountNumbers = tokenisationService.detokenise(
                Arrays.asList(token1, token2)
        );

        // Assert
        assertNotNull(accountNumbers);
        assertEquals(2, accountNumbers.size());
        assertEquals(account1, accountNumbers.get(0));
        assertEquals(account2, accountNumbers.get(1));
    }

    @Test
    void testDetokenise_NonExistingToken_ReturnsNull() {
        // Arrange
        String invalidToken = "InvalidTokenXYZ123";

        when(tokenRepository.findByToken(invalidToken))
                .thenReturn(Optional.empty());

        // Act
        List<String> accountNumbers = tokenisationService.detokenise(
                Arrays.asList(invalidToken)
        );

        // Assert
        assertNotNull(accountNumbers);
        assertEquals(1, accountNumbers.size());
        assertNull(accountNumbers.get(0));
    }

    @Test
    void testTokenise_EmptyList_ReturnsEmptyList() {
        // Act
        List<String> tokens = tokenisationService.tokenise(Arrays.asList());

        // Assert
        assertNotNull(tokens);
        assertTrue(tokens.isEmpty());
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testDetokenise_EmptyList_ReturnsEmptyList() {
        // Act
        List<String> accountNumbers = tokenisationService.detokenise(Arrays.asList());

        // Assert
        assertNotNull(accountNumbers);
        assertTrue(accountNumbers.isEmpty());
    }
}
