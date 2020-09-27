package com.csdg1t3.ryverbankapi.account;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class that allows us to store accounts as persistent data through JPA
 * Methods do not have to be explicitly declared, supports save(), findBy() and delete() 
 * operations
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findById(Long id);

    
}