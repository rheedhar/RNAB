package com.rnab.rnab.repository;

import com.rnab.rnab.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
