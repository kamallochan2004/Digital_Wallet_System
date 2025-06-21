package com.assignment.Digitalwallet.Controller;
import com.assignment.Digitalwallet.Dto.*;
import com.assignment.Digitalwallet.Model.Product;
import com.assignment.Digitalwallet.Model.User;
import com.assignment.Digitalwallet.Service.ProductService;
import com.assignment.Digitalwallet.Service.TransactionService;
import com.assignment.Digitalwallet.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DigitalWalletController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final ProductService productService;

    public DigitalWalletController(UserService userService, TransactionService transactionService, ProductService productService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.productService = productService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request.getUsername(), request.getPassword());
        return new ResponseEntity<>(Collections.singletonMap("message", "User registered successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/fund")
    public ResponseEntity<Map<String, BigDecimal>> fundAccount(@AuthenticationPrincipal User user, @Valid @RequestBody FundRequest request) {
        BigDecimal newBalance = userService.fundAccount(user, request.getAmt());
        transactionService.recordFundingTransaction(user, request.getAmt(), newBalance);
        return ResponseEntity.ok(Collections.singletonMap("balance", newBalance));
    }

    @PostMapping("/pay")
    public ResponseEntity<Map<String, BigDecimal>> payAnotherUser(@AuthenticationPrincipal User user, @Valid @RequestBody PayRequest request) {
        BigDecimal newBalance = transactionService.payAnotherUser(user, request.getTo(), request.getAmt());
        return ResponseEntity.ok(Collections.singletonMap("balance", newBalance));
    }

    @GetMapping("/bal")
    public ResponseEntity<TransactionService.ConvertedBalanceResponse> checkBalance(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String currency) {
        TransactionService.ConvertedBalanceResponse response = transactionService.checkBalance(user, currency);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/stmt")
    public ResponseEntity<List<TransactionResponseDTO>> viewTransactionHistory(@AuthenticationPrincipal User user) {
        List<TransactionResponseDTO> transactions = transactionService.viewTransactionHistory(user);
        return ResponseEntity.ok(transactions);
    }
    @PostMapping("/product")
    public ResponseEntity<Map<String, String>> addProduct(@Valid @RequestBody AddProductRequest request) {
        Product newProduct = Product.builder()
            .name(request.getName())
            .price(request.getPrice())
            .description(request.getDescription())
            .build();
            
        productService.addProduct(newProduct);
        return new ResponseEntity<>(Map.of("id", newProduct.getId().toString(), "message", "Product added"), HttpStatus.CREATED);
    }
    @GetMapping("/product")
    public ResponseEntity<List<Product>> listAllProducts() {
        List<Product> products = productService.listAllProducts();
        return ResponseEntity.ok(products);
    }
    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buyProduct(@AuthenticationPrincipal User user, @Valid @RequestBody BuyProductRequest request) {
        BigDecimal newBalance = productService.buyProduct(user, request.getProduct_id());
        return ResponseEntity.ok(Map.of("message", "Product purchased", "balance", newBalance));
    }
}