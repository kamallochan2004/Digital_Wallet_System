package com.assignment.Digitalwallet.Repository;

import com.assignment.Digitalwallet.Model.Transaction; 
import com.assignment.Digitalwallet.Model.User; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByTimestampDesc(User user);
}