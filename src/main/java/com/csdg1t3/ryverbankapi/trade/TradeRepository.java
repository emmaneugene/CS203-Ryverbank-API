package com.csdg1t3.ryverbankapi.trade;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access object
 */
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long>{
    Trade save(Trade trade);
    void deleteById(Long id);
    List<Trade> findAll();
    Optional<Trade> findById(Long id);
    List<Trade> findByAccountId(Long id);
    List<Trade> findByActionAndSymbolAndStatus(String action, String symbol, String status);
    List<Trade> findByStatus(String status);
 
}

