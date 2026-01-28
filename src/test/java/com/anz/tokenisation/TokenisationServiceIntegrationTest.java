package com.anz.tokenisation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Test App End to End with DB (H2 in memory).
@SpringBootTest
@AutoConfigureMockMvc
class TokenisationServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFullTokenisationFlow() throws Exception {
        // Step 1: Tokenise account numbers.
        List<String> accountNumbers = Arrays.asList(
                "4111-1111-1111-1111",
                "4444-3333-2222-1111",
                "4444-1111-2222-3333"
        );

        MvcResult tokeniseResult = mockMvc.perform(post("/tokenise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountNumbers)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String tokeniseResponse = tokeniseResult.getResponse().getContentAsString();
        List<String> tokens = objectMapper.readValue(
                tokeniseResponse, 
                new TypeReference<List<String>>() {}
        );

        // Verify tokens created.
        assertNotNull(tokens);
        assertEquals(3, tokens.size());
        tokens.forEach(token -> {
            assertNotNull(token);
            assertEquals(32, token.length());
        });

        // Step 2: Detokenise tokens back to account numbers.
        MvcResult detokeniseResult = mockMvc.perform(post("/detokenise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokens)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String detokeniseResponse = detokeniseResult.getResponse().getContentAsString();
        List<String> retrievedAccountNumbers = objectMapper.readValue(
                detokeniseResponse, 
                new TypeReference<List<String>>() {}
        );

        // Verify received original account numbers.
        assertNotNull(retrievedAccountNumbers);
        assertEquals(accountNumbers.size(), retrievedAccountNumbers.size());
        assertEquals(accountNumbers, retrievedAccountNumbers);
    }

    @Test
    void testTokenPersistence_SameAccountNumberGetsSameToken() throws Exception {
        // Tokenise same account number twice.
        List<String> accountNumbers = Arrays.asList("4111-1111-1111-1111");

        // First tokenisation.
        MvcResult result1 = mockMvc.perform(post("/tokenise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountNumbers)))
                .andExpect(status().isOk())
                .andReturn();

        List<String> tokens1 = objectMapper.readValue(
                result1.getResponse().getContentAsString(),
                new TypeReference<List<String>>() {}
        );

        // Second tokenisation (same account).
        MvcResult result2 = mockMvc.perform(post("/tokenise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountNumbers)))
                .andExpect(status().isOk())
                .andReturn();

        List<String> tokens2 = objectMapper.readValue(
                result2.getResponse().getContentAsString(),
                new TypeReference<List<String>>() {}
        );

        // Verify same token is returned.
        assertEquals(tokens1.get(0), tokens2.get(0));
    }

    @Test
    void testDetokenise_InvalidToken_ReturnsNull() throws Exception {
        // Try detokenise token that doesn't exist.
        List<String> invalidTokens = Arrays.asList("InvalidToken12345678901234567890");

        MvcResult result = mockMvc.perform(post("/detokenise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTokens)))
                .andExpect(status().isOk())
                .andReturn();

        List<String> accountNumbers = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<String>>() {}
        );

        // Return null for invalid token.
        assertEquals(1, accountNumbers.size());
        assertNull(accountNumbers.get(0));
    }
}
