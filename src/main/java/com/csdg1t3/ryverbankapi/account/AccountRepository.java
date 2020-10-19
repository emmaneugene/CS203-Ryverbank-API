package com.csdg1t3.ryverbankapi.account;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class that allows us to store accounts as persistent data through JPA
 * Methods do not have to be explicitly declared, supports save(), findBy() and delete() 
 * operations
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerId(Long id);

    // Using Optional - the return value of this method may contain a null value
    Optional<Account> findById(Long id);
}