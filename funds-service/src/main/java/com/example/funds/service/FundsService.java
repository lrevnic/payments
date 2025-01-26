package com.example.funds.service;

import com.example.funds.model.*;
import com.example.funds.repository.WalletRepository;
import com.example.funds.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class FundsService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public FundsService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction credit(Long walletId, String currencyCode, BigDecimal amount) {
        Wallet wallet = walletRepository.findByIdAndCurrencyCode(walletId, currencyCode)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setReferenceId(UUID.randomUUID().toString());

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction debit(Long walletId, String currencyCode, BigDecimal amount) {
        Wallet wallet = walletRepository.findByIdAndCurrencyCode(walletId, currencyCode)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setReferenceId(UUID.randomUUID().toString());

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction reverse(String referenceId) {
        Transaction originalTransaction = transactionRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (originalTransaction.getStatus() == TransactionStatus.REVERSED) {
            throw new IllegalStateException("Transaction already reversed");
        }

        Wallet wallet = originalTransaction.getWallet();
        BigDecimal amount = originalTransaction.getAmount();

        Transaction reverseTransaction = new Transaction();
        reverseTransaction.setWallet(wallet);
        reverseTransaction.setAmount(amount);
        reverseTransaction.setTransactionType(TransactionType.REVERSE);
        reverseTransaction.setStatus(TransactionStatus.COMPLETED);
        reverseTransaction.setReferenceId(UUID.randomUUID().toString());

        if (originalTransaction.getTransactionType() == TransactionType.CREDIT) {
            wallet.setBalance(wallet.getBalance().subtract(amount));
        } else if (originalTransaction.getTransactionType() == TransactionType.DEBIT) {
            wallet.setBalance(wallet.getBalance().add(amount));
        }

        originalTransaction.setStatus(TransactionStatus.REVERSED);
        walletRepository.save(wallet);
        transactionRepository.save(originalTransaction);
        return transactionRepository.save(reverseTransaction);
    }

    @Transactional
    public Transaction transfer(Long sourceWalletId, Long targetWalletId, String currencyCode, BigDecimal amount) {
        Wallet sourceWallet = walletRepository.findByIdAndCurrencyCode(sourceWalletId, currencyCode)
                .orElseThrow(() -> new IllegalArgumentException("Source wallet not found"));
        Wallet targetWallet = walletRepository.findByIdAndCurrencyCode(targetWalletId, currencyCode)
                .orElseThrow(() -> new IllegalArgumentException("Target wallet not found"));

        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(sourceWallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setReferenceId(UUID.randomUUID().toString());

        sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));
        targetWallet.setBalance(targetWallet.getBalance().add(amount));

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);
        return transactionRepository.save(transaction);
    }
}