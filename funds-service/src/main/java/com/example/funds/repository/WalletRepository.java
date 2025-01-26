package com.example.funds.repository;

import com.example.funds.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findByIdAndCurrencyCode(Long id, String currencyCode);
    
    Optional<Wallet> findByCustomerIdAndCurrencyCode(Long customerId, String currencyCode);
}