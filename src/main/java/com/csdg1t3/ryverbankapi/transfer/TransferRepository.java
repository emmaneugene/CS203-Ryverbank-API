package com.csdg1t3.ryverbankapi.transfer;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Allows us to store transfers as pesistent data through JPA.
 * Methods to not have to be explicitly declared, supports save(), findBy(), and delete() operations
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long>{
    Transfer save(Transfer transfer);
    List<Transfer> findBySenderId(Long id);
    List<Transfer> findByReceiverId(Long id);
    // List<Transfer> findAll();
    
    Optional<Transfer> findByIdAndSenderId(Long id, Long senderId);
    Optional<Transfer> findByIdAndReceiverId(Long id, Long receiverId);
}