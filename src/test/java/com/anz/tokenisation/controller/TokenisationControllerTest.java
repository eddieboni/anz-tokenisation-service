package com.anz.tokenisation.controller;

import com.anz.tokenisation.service.TokenisationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Test Controller Layer (HTTP API) Without Starting the Full Application.
@WebMvcTest(TokenisationController.class)
class TokenisationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenisationService tokenisationService;

    @Test
    void testTokenize_ValidInput_ReturnsTokens() throws Exception {
        // Arrange
        List<String> accountNumbers = Arrays.asList(
                "4111-1111-1111-1111",
                "4444-3333-2222-1111"
        );
        List<String> tokens = Arrays.asList(
                "fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy",
                "L4hKuBJHxe67ENSKLVbdIH8NhFefPui2"
        );

        when(tokenisationService.tokenise(anyList())).thenReturn(tokens);

        // Act & Assert
        mockMvc.perform(post("/tokenise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountNumbers)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(tokens.get(0)))
                .andExpect(jsonPath("$[1]").value(tokens.get(1)));
    }

    @Test
    void testTokenize_EmptyInput_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/tokenise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDetokenize_ValidInput_ReturnsAccountNumbers() throws Exception {
        // Arrange
        List<String> tokens = Arrays.asList(
                "fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy",
                "L4hKuBJHxe67ENSKLVbdIH8NhFefPui2"
        );
        List<String> accountNumbers = Arrays.asList(
                "4111-1111-1111-1111",
                "4444-3333-2222-1111"
        );

        when(tokenisationService.detokenise(anyList())).thenReturn(accountNumbers);

        // Act & Assert
        mockMvc.perform(post("/detokenise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokens)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(accountNumbers.get(0)))
                .andExpect(jsonPath("$[1]").value(accountNumbers.get(1)));
    }

    @Test
    void testDetokenise_EmptyInput_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/detokenise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest());
    }
}
