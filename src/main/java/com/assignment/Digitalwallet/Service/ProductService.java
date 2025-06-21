package com.assignment.Digitalwallet.Service;

import com.assignment.Digitalwallet.Exception.InsufficientFundsException;
import com.assignment.Digitalwallet.Exception.ProductNotFoundException;
import com.assignment.Digitalwallet.Model.Product;
import com.assignment.Digitalwallet.Model.User;
import com.assignment.Digitalwallet.Repository.ProductRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final UserService userService;
    private final TransactionService transactionService;
    public ProductService(ProductRepo productRepo, TransactionService transactionService, UserService userService) {
        this.productRepo = productRepo;
        this.transactionService= transactionService;
        this.userService = userService;
    }
    @Transactional
    public Product addProduct(Product product) {
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive.");
        }
        return productRepo.save(product); //adding of products to the database
    }

    public List<Product> listAllProducts() {
        return productRepo.findAll(); //finding all the products added to the database
    }

     @Transactional
    public BigDecimal buyProduct(User user, Long productId) {
        Objects.requireNonNull(user, "User cannot be null for product purchase.");
        Objects.requireNonNull(productId, "Product ID cannot be null for product purchase.");
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));
        BigDecimal productPrice = product.getPrice();
        if (user.getBalance().compareTo(productPrice) < 0) {
            throw new InsufficientFundsException("Insufficient balance to purchase this product.");
        }
        user.setBalance(user.getBalance().subtract(productPrice));
        userService.saveUser(user);
            transactionService.recordProductPurchaseTransaction(
            user,
            productPrice,
            user.getBalance(),
            product.getName(),
            product 
        );
        return user.getBalance();
    }
}