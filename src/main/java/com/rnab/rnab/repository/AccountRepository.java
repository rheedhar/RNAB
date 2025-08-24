package com.rnab.rnab.repository;

import com.rnab.rnab.model.Account;
import com.rnab.rnab.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIdAndUser(Long id, User user);
    List<Account> findByUser(User user);
}
