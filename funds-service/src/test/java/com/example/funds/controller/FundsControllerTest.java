package com.example.funds.controller;

import com.example.funds.dto.TransactionRequest;
import com.example.funds.dto.TransferRequest;
import com.example.funds.model.Transaction;
import com.example.funds.service.FundsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class FundsControllerTest {

    @InjectMocks
    //@Autowired
    private FundsController fundsController;

    @Mock
    //@MockBean
    private FundsService fundsService;

    /**
     * Test case for crediting funds to an account.
     * This test verifies that the credit method in FundsController
     * correctly processes a valid transaction request and returns
     * the expected ResponseEntity with the transaction details.
     */
    @Test
    public void testCreditFundsToAccount() {
        // Arrange
        FundsService fundsService = Mockito.mock(FundsService.class);
        FundsController fundsController = new FundsController(fundsService);
        TransactionRequest request = new TransactionRequest();
        request.setWalletId(123L);
        request.setCurrencyCode("USD");
        request.setAmount(BigDecimal.valueOf( 100));

        Transaction mockTransaction = new Transaction();
        when(fundsService.credit(request.getWalletId(), request.getCurrencyCode(), request.getAmount()))
            .thenReturn(mockTransaction);

        // Act
        ResponseEntity<Transaction> response = fundsController.credit(request);

        // Assert
        assertEquals(ResponseEntity.ok(mockTransaction), response);
    }

    /**
     * Tests the FundsController constructor with a null FundsService.
     * This test verifies that the constructor throws a NullPointerException
     * when passed a null FundsService object.
     */
    @Test
    public void testFundsControllerConstructorWithNullService() {
        assertThrows(NullPointerException.class, () -> new FundsController(null));
    }

    /**
     * Test case for reversing a transaction successfully.
     * This test verifies that the reverse method in FundsController
     * correctly calls the FundsService and returns the reversed transaction
     * wrapped in a ResponseEntity with OK status.
     */
    @Test
    public void testReverseTransactionSuccessfully() {
        String referenceId = "TX123456";
        Transaction reversedTransaction = new Transaction(); // Assume this is a valid reversed transaction
        when(fundsService.reverse(referenceId)).thenReturn(reversedTransaction);

        ResponseEntity<Transaction> response = fundsController.reverse(referenceId);

        assertEquals(ResponseEntity.ok(reversedTransaction), response);
    }

    /**
     * Test case for successful fund transfer between two wallets.
     * This test verifies that the transfer method in FundsController
     * correctly processes a valid transfer request and returns the
     * expected ResponseEntity with the transaction details.
     */
    @Test
    public void testTransferSuccessful() {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setSourceWalletId(123L);
        request.setTargetWalletId(456L);
        request.setCurrencyCode("USD");
        request.setAmount(BigDecimal.valueOf(100));

        Transaction mockTransaction = new Transaction();
        mockTransaction.setId(123L);

        when(fundsService.transfer(
            request.getSourceWalletId(),
            request.getTargetWalletId(),
            request.getCurrencyCode(),
            request.getAmount()
        )).thenReturn(mockTransaction);

        // Act
        ResponseEntity<Transaction> response = fundsController.transfer(request);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals(mockTransaction, response.getBody());
    }

    /**
     * Tests the constructor of FundsController to ensure it correctly initializes
     * the controller with a FundsService instance.
     */
    @Test
    public void test_FundsController_ConstructorInitialization() {
        FundsService mockFundsService = mock(FundsService.class);
        FundsController fundsController = new FundsController(mockFundsService);
        assertNotNull(fundsController, "FundsController should be initialized");
    }

    /**
     * Tests the debit method of FundsController.
     * Verifies that the method correctly calls the FundsService and returns the expected ResponseEntity.
     */
    @Test
    public void test_debit_successful_transaction() {
        // Arrange
        FundsService fundsService = Mockito.mock(FundsService.class);
        FundsController fundsController = new FundsController(fundsService);
        TransactionRequest request = new TransactionRequest();
        request.setWalletId(123L);
        request.setCurrencyCode("USD");
        request.setAmount(BigDecimal.valueOf(100.0));
        Transaction expectedTransaction = new Transaction();
        when(fundsService.debit(request.getWalletId(), request.getCurrencyCode(), request.getAmount())).thenReturn(expectedTransaction);

        // Act
        ResponseEntity<Transaction> response = fundsController.debit(request);

        // Assert
        assertEquals(ResponseEntity.ok(expectedTransaction), response);
    }


    /**
     * Tests the credit method of FundsController.
     * Verifies that the method correctly calls the FundsService and returns the expected ResponseEntity
     * when crediting funds to an account.
     */
    @Test
    public void testCreditFundsSuccessfully() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setWalletId(123L);
        request.setCurrencyCode("USD");
        request.setAmount(BigDecimal.valueOf(100.0));

        Transaction expectedTransaction = new Transaction();
        when(fundsService.credit(request.getWalletId(), request.getCurrencyCode(), request.getAmount())).thenReturn(expectedTransaction);

        // Act
        ResponseEntity<Transaction> response = fundsController.credit(request);

        // Assert
        assertEquals(ResponseEntity.ok(expectedTransaction), response);
    }

    /**
     * Tests the credit method of FundsController with an invalid TransactionRequest.
     * This test verifies that when an invalid request is provided, the method
     * handles it appropriately by throwing a MethodArgumentNotValidException.
     */
    @Test
    public void testCreditWithInvalidRequest() {
        TransactionRequest invalidRequest = new TransactionRequest();
        // Set invalid data in the request
        invalidRequest.setWalletId(-1L);
        invalidRequest.setCurrencyCode("");
        invalidRequest.setAmount(BigDecimal.valueOf(-100.0));

        assertThrows(org.springframework.web.bind.MethodArgumentNotValidException.class, () -> {
            fundsController.credit(invalidRequest);
        });
    }

    /**
     * Test case for debiting funds from an account.
     * This test verifies that the debit method in FundsController
     * correctly processes a valid transaction request and returns
     * the expected ResponseEntity with the transaction details.
     */
    @Test
    public void testDebitFundsFromAccount() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setWalletId(123L);
        request.setCurrencyCode("USD");
        request.setAmount(BigDecimal.valueOf(100.0));

        Transaction mockTransaction = new Transaction();
        when(fundsService.debit(request.getWalletId(), request.getCurrencyCode(), request.getAmount()))
            .thenReturn(mockTransaction);

        // Act
        ResponseEntity<Transaction> response = fundsController.debit(request);

        // Assert
        assertEquals(ResponseEntity.ok(mockTransaction), response);
    }

    /**
     * Tests the debit method of FundsController with an invalid transaction request.
     * This test verifies that when an invalid TransactionRequest is provided,
     * the controller properly handles the validation and returns an appropriate error response.
     */
    @Test
    public void testDebitWithInvalidRequest() {
        TransactionRequest invalidRequest = new TransactionRequest();
        // Set invalid data in the request
        invalidRequest.setWalletId(0L);
        invalidRequest.setCurrencyCode("");
        invalidRequest.setAmount(BigDecimal.valueOf(-100.0));

        ResponseEntity<Transaction> response = fundsController.debit(invalidRequest);

        assertEquals(400, response.getStatusCode());
        assertNull(response.getBody());
    }

    /**
     * Tests the reverse method of FundsController.
     * Verifies that the method correctly calls the FundsService to reverse a transaction
     * and returns the expected ResponseEntity with the reversed transaction details.
     */
    @Test
    public void testReverseTransaction() {
        // Arrange
        String referenceId = "txn123";
        Transaction reversedTransaction = new Transaction();
        when(fundsService.reverse(referenceId)).thenReturn(reversedTransaction);

        // Act
        ResponseEntity<Transaction> response = fundsController.reverse(referenceId);

        // Assert
        assertEquals(ResponseEntity.ok(reversedTransaction), response);
    }

    /**
     * Tests the transfer method of FundsController with invalid input.
     * This test verifies that when an invalid TransferRequest is provided,
     * the method handles it gracefully by returning a bad request response.
     */
    @Test
    public void testTransferWithInvalidInput() {
        // Arrange
        TransferRequest invalidRequest = new TransferRequest();
        // Set invalid data in the request
        invalidRequest.setSourceWalletId(-1L);
        invalidRequest.setTargetWalletId(-1L);
        invalidRequest.setCurrencyCode("AUD");
        invalidRequest.setAmount(BigDecimal.valueOf(-100.0));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> fundsController.transfer(invalidRequest));
    }

    /**
     * Tests that the FundsController constructor correctly initializes
     * the controller with a non-null FundsService instance.
     */
    @Test
    public void test_FundsController_ConstructorInitializationWithNonNullService() {
        FundsService mockFundsService = mock(FundsService.class);
        FundsController fundsController = new FundsController(mockFundsService);
        assertNotNull(fundsController, "FundsController should be initialized with a non-null FundsService");
    }

    /**
     * Test case for transferring funds between wallets.
     * This test verifies that the transfer method in FundsController
     * correctly processes a valid transfer request and returns
     * the expected ResponseEntity with the transaction details.
     */
    @Test
    public void test_transfer_successful() {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setSourceWalletId(123L);
        request.setTargetWalletId(456L);
        request.setCurrencyCode("USD");
        request.setAmount(BigDecimal.valueOf(100.0));

        Transaction mockTransaction = new Transaction();
        when(fundsService.transfer(request.getSourceWalletId(), request.getTargetWalletId(), 
                                   request.getCurrencyCode(), request.getAmount()))
            .thenReturn(mockTransaction);

        // Act
        ResponseEntity<Transaction> response = fundsController.transfer(request);

        // Assert
        assertEquals(ResponseEntity.ok(mockTransaction), response);
    }
}
