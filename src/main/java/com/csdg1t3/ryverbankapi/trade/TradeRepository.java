package com.csdg1t3.ryverbankapi.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Data access object
 */
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long>{
    Trade save(Trade trade);
    void deleteById(Long id);
    List<Trade> findAll();
    Optional<Trade> findById(Long id);
    List<Trade> findByCustomerId(Long id);
    List<Trade> findByAccountId(Long id);
    List<Trade> findByStatusIn(Collection<String> statuses);
    List<Trade> findByActionAndSymbolAndStatusIn(String action, String symbol, 
    Collection<String> statuses);
    List<Trade> findByActionAndSymbolAndBidAndStatusIn(String action, String symbol, Double bid, 
    Collection<String> statuses);
    List<Trade> findByActionAndSymbolAndAskAndStatusIn(String action, String symbol, Double ask,
    Collection<String> statuses);
    List<Trade> findByActionAndSymbolAndCustomerIdAndStatusIn(String Action, String symbol, Long id, 
    Collection<String> statuses);
    boolean existsById(Long id);
}

