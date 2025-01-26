package com.example.funds.controller;

import com.example.funds.model.Wallet;
import com.example.funds.repository.WalletRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@Tag(name = "Wallet Management", description = "APIs for managing wallets")
public class WalletController {

    private final WalletRepository walletRepository;

    public WalletController(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Operation(summary = "Create a new wallet", description = "Creates a new wallet for a customer with specified currency")
    @PostMapping
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody Wallet wallet) {
        Wallet savedWallet = walletRepository.save(wallet);
        return ResponseEntity.ok(savedWallet);
    }

    @Operation(summary = "Get a wallet by ID", description = "Retrieves wallet details by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long id) {
        return walletRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all wallets", description = "Retrieves all wallets")
    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets() {
        List<Wallet> wallets = walletRepository.findAll();
        return ResponseEntity.ok(wallets);
    }

    @Operation(summary = "Update a wallet", description = "Updates an existing wallet")
    @PutMapping("/{id}")
    public ResponseEntity<Wallet> updateWallet(@PathVariable Long id, @Valid @RequestBody Wallet walletDetails) {
        return walletRepository.findById(id)
                .map(wallet -> {
                    wallet.setCustomerId(walletDetails.getCustomerId());
                    wallet.setCurrencyCode(walletDetails.getCurrencyCode());
                    wallet.setBalance(walletDetails.getBalance());
                    return ResponseEntity.ok(walletRepository.save(wallet));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a wallet", description = "Deletes a wallet by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        return walletRepository.findById(id)
                .map(wallet -> {
                    walletRepository.delete(wallet);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get wallet by customer ID and currency", description = "Retrieves wallet details by customer ID and currency code")
    @GetMapping("/search")
    public ResponseEntity<Wallet> getWalletByCustomerAndCurrency(
            @RequestParam Long customerId,
            @RequestParam String currencyCode) {
        return walletRepository.findByCustomerIdAndCurrencyCode(customerId, currencyCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}