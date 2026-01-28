package com.anz.tokenisation.repository;

import com.anz.tokenisation.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Database Access Layer.
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    
    Optional<Token> findByAccountNumber(String accountNumber);
    
    Optional<Token> findByToken(String token);
    
    List<Token> findByTokenIn(List<String> tokens);
}
