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
    void deleteById(Long id);
    //List<User> findAll();
    List<User> findAllByAuthorities(String authorities);

    // Using Optional - the return value of this method may contain a null value
    Optional<User> findById(Long id);

    // Using Optional - the return value of this method may contain a null value
    Optional<User> findByUsername(String username);

    // Using Optional - the return value of this method may contain a null value
    Optional<User> findByAuthorities(String authorities);
 
}