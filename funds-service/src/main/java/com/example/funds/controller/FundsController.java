package com.example.funds.controller;

import com.example.funds.dto.TransactionRequest;
import com.example.funds.dto.TransferRequest;
import com.example.funds.model.Transaction;
import com.example.funds.service.FundsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/funds")
@Tag(name = "Funds Management", description = "APIs for managing funds operations")
public class FundsController {
    private final FundsService fundsService;

    public FundsController(FundsService fundsService) {
        this.fundsService = fundsService;
    }

    @Operation(summary = "Credit funds to an account", description = "Credits the specified amount to the given account")
    @PostMapping("/credit")
    public ResponseEntity<Transaction> credit(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = fundsService.credit(
            request.getWalletId(),
            request.getCurrencyCode(),
            request.getAmount()
        );
        return ResponseEntity.ok(transaction);
    }

    @Operation(summary = "Debit funds from an account", description = "Debits the specified amount from the given account")
    @PostMapping("/debit")
    public ResponseEntity<Transaction> debit(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = fundsService.debit(
            request.getWalletId(),
            request.getCurrencyCode(),
            request.getAmount()
        );
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@Valid @RequestBody TransferRequest request) {
        Transaction transaction = fundsService.transfer(
            request.getSourceWalletId(),
            request.getTargetWalletId(),
            request.getCurrencyCode(),
            request.getAmount()
        );
        return ResponseEntity.ok(transaction);
    }

    @Operation(summary = "Reverse a transaction", description = "Reverses a previously executed transaction using its reference ID")
    @PostMapping("/reverse/{referenceId}")
    public ResponseEntity<Transaction> reverse(@PathVariable String referenceId) {
        Transaction transaction = fundsService.reverse(referenceId);
        return ResponseEntity.ok(transaction);
    }
}