package com.assignment.Digitalwallet.Service;

import com.assignment.Digitalwallet.Exception.UserAlreadyExistsException;
import com.assignment.Digitalwallet.Model.User;
import com.assignment.Digitalwallet.Repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String plainPassword) {
        if (userRepo.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username '" + username + "' already exists.");
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(plainPassword)); //here hashing happens
        newUser.setBalance(BigDecimal.ZERO);
        return userRepo.save(newUser);
    }

    @Transactional
    public BigDecimal fundAccount(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Funding amount must be positive.");
        }

        user.setBalance(user.getBalance().add(amount));
        userRepo.save(user); 
        return user.getBalance(); //returns the new balance
    }

    public Optional<User> findByUsername(String username) { //to check if the recipient exists
        return userRepo.findByUsername(username); 
    }

    @Transactional
    public User saveUser(User user) { //to save the transaction record of credit and debit for user 
        return userRepo.save(user);
    }
}