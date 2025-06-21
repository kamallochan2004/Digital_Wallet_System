package com.assignment.Digitalwallet.Service;

import com.assignment.Digitalwallet.Dto.TransactionResponseDTO;
import com.assignment.Digitalwallet.Exception.InsufficientFundsException;
import com.assignment.Digitalwallet.Exception.InvalidCurrencyException;
import com.assignment.Digitalwallet.Exception.UserNotFoundException;
import com.assignment.Digitalwallet.Model.Product;
import com.assignment.Digitalwallet.Model.Transaction;
import com.assignment.Digitalwallet.Model.TransactionKind;
import com.assignment.Digitalwallet.Model.User;
import com.assignment.Digitalwallet.Repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${currencyapi.api-key}")
    private String currencyApiKey;

    @Value("${currencyapi.base-url}")
    private String currencyApiBaseUrl;

    public TransactionService(TransactionRepo transactionRepo, UserService userService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.transactionRepo = transactionRepo;
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void recordFundingTransaction(User user, BigDecimal amount, BigDecimal newBalance) {
        Objects.requireNonNull(user, "User cannot be null for funding transaction.");
        Objects.requireNonNull(amount, "Amount cannot be null for funding transaction.");
        Objects.requireNonNull(newBalance, "New balance cannot be null for funding transaction.");
        Transaction transaction = Transaction.builder()
            .user(user)
            .kind(TransactionKind.CREDIT)
            .amount(amount)
            .updatedBalance(newBalance)
            .build();
        transactionRepo.save(transaction);
        user.addTransaction(transaction);
    }

    @Transactional
    public BigDecimal payAnotherUser(User sender, String receiverUsername, BigDecimal amount) {
        Objects.requireNonNull(sender, "Sender user cannot be null.");
        Objects.requireNonNull(receiverUsername, "Receiver username cannot be null.");
        Objects.requireNonNull(amount, "Amount cannot be null.");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        else if (sender.getUsername().equals(receiverUsername)) {
            throw new IllegalArgumentException("Cannot transfer money to yourself.");
        }
        else if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds to complete the transfer.");
        }
        User receiver = userService.findByUsername(receiverUsername)
                .orElseThrow(() -> new UserNotFoundException("Recipient '" + receiverUsername + "' not found."));

        sender.setBalance(sender.getBalance().subtract(amount)); //money debits from the sender 
        userService.saveUser(sender);
        receiver.setBalance(receiver.getBalance().add(amount));//money credits to the reciever
        userService.saveUser(receiver);
        
        //logging of the above transaction
        recordPaymentTransaction(sender, TransactionKind.DEBIT, amount, sender.getBalance(), receiverUsername);
        recordPaymentTransaction(receiver, TransactionKind.CREDIT, amount, receiver.getBalance(), sender.getUsername());

        return sender.getBalance();
    }

    @Transactional
    public void recordPaymentTransaction(User user, TransactionKind kind, BigDecimal amount, BigDecimal updatedBalance, String relatedUserUsername) {
        Objects.requireNonNull(user, "User cannot be null for payment transaction.");
        Objects.requireNonNull(kind, "Transaction kind cannot be null.");
        Objects.requireNonNull(amount, "Amount cannot be null for payment transaction.");
        Objects.requireNonNull(updatedBalance, "Updated balance cannot be null for payment transaction.");
        Transaction transaction = Transaction.builder()
                                .user(user)
                                .kind(kind)
                                .amount(amount)
                                .updatedBalance(updatedBalance)
                                .relatedUserUsername(relatedUserUsername)
                                .build();
        transactionRepo.save(transaction);
        user.addTransaction(transaction);
    }

        @Transactional
    public void recordProductPurchaseTransaction(User buyer, BigDecimal productPrice, BigDecimal updatedBalance, String productName, Product product) {
        Objects.requireNonNull(buyer, "Buyer user cannot be null.");
        Objects.requireNonNull(productPrice, "Product price cannot be null.");
        Objects.requireNonNull(updatedBalance, "Updated balance cannot be null for product purchase.");
        Objects.requireNonNull(productName, "Product name cannot be null for product purchase.");
        Objects.requireNonNull(product, "Product cannot be null for product purchase.");
        
        Transaction transaction = Transaction.builder()
                .user(buyer)
                .kind(TransactionKind.DEBIT)
                .amount(productPrice)
                .updatedBalance(updatedBalance)
                .description("Purchased: " + productName)
                .product(product)  // Now using the parameter
                .build();
                
        transactionRepo.save(transaction);
        buyer.addTransaction(transaction);
    }
    public static class ConvertedBalanceResponse {
        private BigDecimal balance;
        private String currency;
        public ConvertedBalanceResponse(BigDecimal balance, String currency) {
            this.balance = balance;
            this.currency = currency;
        }
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    public ConvertedBalanceResponse checkBalance(User user, String targetCurrency) {
        Objects.requireNonNull(user, "User cannot be null when checking balance.");

        BigDecimal balanceINR = user.getBalance();
        String defaultCurrency = "INR";

        if (targetCurrency == null || targetCurrency.trim().isEmpty() || defaultCurrency.equalsIgnoreCase(targetCurrency)) {
            return new ConvertedBalanceResponse(balanceINR, defaultCurrency);
        }

        BigDecimal convertedBalance = convertCurrency(balanceINR, defaultCurrency, targetCurrency);
        return new ConvertedBalanceResponse(convertedBalance, targetCurrency.toUpperCase());
    }

    private BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (!isValidCurrencyFormat(toCurrency)) {
            throw new InvalidCurrencyException(toCurrency, "Currency code must be a valid 3-letter ISO code.");
        }
        String url = String.format("%s/latest?apikey=%s&base_currency=%s&currencies=%s",
                currencyApiBaseUrl, currencyApiKey, fromCurrency.toUpperCase(), toCurrency.toUpperCase());
        try {
            String responseBody = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode rateNode = root.path("data").path(toCurrency.toUpperCase()).path("value");
    
            if (rateNode.isMissingNode() || !rateNode.isNumber()) {
                throw new InvalidCurrencyException(toCurrency, 
                    "Currency not supported by the conversion service. Response: " + responseBody);
            }
            BigDecimal rate = rateNode.decimalValue();
            return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        } catch (RestClientException e) {
            throw new InvalidCurrencyException(toCurrency, e);
        } catch (JsonProcessingException e) {
            throw new InvalidCurrencyException(toCurrency, "Error parsing API response: " + e.getMessage());
        }
    }
    private boolean isValidCurrencyFormat(String currency) {
        return currency != null && currency.matches("[A-Za-z]{3}");
    }
    
    public List<TransactionResponseDTO> viewTransactionHistory(User user) {
        Objects.requireNonNull(user, "User cannot be null when viewing transaction history.");
        List<Transaction> transactions = transactionRepo.findByUserOrderByTimestampDesc(user);
        return transactions.stream()
                .map(this::mapToTransactionResponseDTO)
                .collect(Collectors.toList());
    }
    private TransactionResponseDTO mapToTransactionResponseDTO(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction cannot be null for DTO mapping.");
        return new TransactionResponseDTO(
                transaction.getKind().name().toLowerCase(),
                transaction.getAmount(),
                transaction.getUpdatedBalance(),
                transaction.getTimestamp()
        );
    }

}