package com.csdg1t3.ryverbankapi.account;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AccountRepository
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    Optional<Account> findById(Long id);

    
}