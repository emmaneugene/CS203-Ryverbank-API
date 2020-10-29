package com.csdg1t3.ryverbankapi.user;

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
public interface UserRepository extends JpaRepository<User, Long>{
    User save(User user);
    boolean existsById(Long id);
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    void deleteById(Long id);
    void deleteByAuthoritiesContaining(String authority);
    
}