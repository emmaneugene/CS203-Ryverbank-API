package com.csdg1t3.ryverbankapi.customer;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Allows us to store customers as persistent data through JPA. 
 * Methods do not have to be explicitly declared, supports save(), findBy() and delete()
 * operations.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
    Customer save(Customer customer);
    void deleteById(Long id);
    List<Customer> findAll();

    // Using Optional - the return value of this method may contain a null value
    Optional<Customer> findById(Long id);
 
}