package com.spreadsheet.demospreadsheet.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);

    UserDetails findByEmail(String email);
}
